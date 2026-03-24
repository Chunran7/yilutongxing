// 定制请求实例（统一处理 Result{code,msg,data} + 自动携带 JWT）
import axios from "axios";
import { ElMessage } from "element-plus";

const baseURL = "/api";

const instance = axios.create({
  baseURL,
  timeout: 15000,
});

// 将可能的 url 归一化为 path（去掉 baseURL 或解析完整 URL）
function normalizePath(url) {
  const u = (url || "").toString();
  if (!u) return "";
  // 完整 URL（包含协议）
  if (u.startsWith("http://") || u.startsWith("https://")) {
    try {
      const p = new URL(u);
      return p.pathname || "";
    } catch (e) {
      return u;
    }
  }
  // 如果包含 baseURL 前缀，去掉它
  if (u.startsWith(baseURL)) return u.slice(baseURL.length) || "/";
  return u;
}

function isAdminUrl(url) {
  const p = normalizePath(url);
  return p.startsWith("/admin");
}

function getAuthTokenByUrl(url) {
  const p = normalizePath(url);
  return isAdminUrl(p)
    ? localStorage.getItem("admin_token")
    : localStorage.getItem("token");
}

// 请求拦截器：自动在请求头中加入 Authorization
instance.interceptors.request.use(
  (config) => {
    const token = getAuthTokenByUrl(config?.url);
    if (token) {
      const auth = token.startsWith("Bearer ") ? token : `Bearer ${token}`;
      config.headers = config.headers || {};

      // axios v1: 可能是 AxiosHeaders，优先用 set
      if (typeof config.headers.set === "function") {
        config.headers.set("Authorization", auth);
        // 兼容：部分旧后端可能读取 token 字段（可留着不影响）
        config.headers.set("token", token);
      } else {
        config.headers["Authorization"] = auth;
        config.headers["token"] = token;
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 响应拦截器：成功直接返回 data，失败抛出 Error(msg)
instance.interceptors.response.use(
  (res) => {
    const payload = res?.data;

    // 约定：Result{code,msg,data}，code=0 为成功
    if (payload && payload.code === 0) return payload.data;

    const msg = payload?.msg || "操作失败";
    return Promise.reject(new Error(msg));
  },
  (error) => {
    const status = error?.response?.status;
    const payload = error?.response?.data;
    const msg = payload?.msg || error?.message || "网络异常，请稍后重试";

    if (status === 401) {
      const currentPath =
        window.location.pathname +
        window.location.search +
        window.location.hash;

      const requestUrl = (error?.config?.url || "").toString();
      const requestPath = normalizePath(requestUrl);
      const inAdminContext =
        window.location.pathname.startsWith("/admin") ||
        isAdminUrl(requestPath);

      // ✅ 管理员：401 时不要在 /admin 页面反复 replace，否则会死循环闪屏
      if (inAdminContext) {
        localStorage.removeItem("admin_token");
        sessionStorage.removeItem("admin_logged_in");

        // 通知页面切回登录框（Admin.vue 监听这个事件）
        window.dispatchEvent(new Event("admin-logout"));

        ElMessage.error(payload?.msg || "需要管理员登录");

        // 如果当前不在 /admin，才跳回 /admin（一般不会走到这）
        if (!window.location.pathname.startsWith("/admin")) {
          const redirect = encodeURIComponent(currentPath);
          window.location.replace(`/admin?redirect=${redirect}`);
        }

        return Promise.reject(new Error(payload?.msg || "需要管理员登录"));
      }

      // ✅ 普通用户已禁用登录验证：不再自动跳转和清除 token
      // 仅当接口明确返回 401 时才显示错误信息，由调用方自行处理
      return Promise.reject(new Error(payload?.msg || msg));
    }

    return Promise.reject(new Error(msg));
  }
);

export default instance;
