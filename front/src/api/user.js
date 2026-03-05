import request from "@/utils/request.js";

// 注册（x-www-form-urlencoded）
export const userRegisterService = (registerData) => {
  const params = new URLSearchParams();
  for (const key in registerData) params.append(key, registerData[key]);
  return request.post("/user/register", params);
};

// 登录（x-www-form-urlencoded）
export const userLoginService = (loginData) => {
  const params = new URLSearchParams();
  for (const key in loginData) params.append(key, loginData[key]);
  return request.post("/user/login", params);
};

// 当前登录用户
export const getMeService = () => request.get("/user/me");

// 用户公开主页（可匿名）
export const getUserProfileService = (id) => request.get(`/user/profile/${id}`);

// 更新个人资料（需要登录）
export const updateProfileService = (data) =>
  request.put("/user/profile", data);

// 关注 / 取消关注（需要登录）
export const toggleFollowService = (id) =>
  request.post(`/user/follow/${id}/toggle`);

// 我关注的人列表（需要登录）
export const getFollowingService = () => request.get("/user/following");

// 上传头像（需要登录）
export const uploadAvatarService = (file) => {
  const fd = new FormData();
  fd.append("file", file);
  return request.post("/user/avatar/upload", fd, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};

// 忘记密码相关
export const sendForgotCodeService = (data) => {
  const params = new URLSearchParams();
  for (const key in data) params.append(key, data[key]);
  return request.post("/user/forgot/send", params);
};

export const verifyForgotCodeService = (data) => {
  const params = new URLSearchParams();
  for (const key in data) params.append(key, data[key]);
  return request.post("/user/forgot/verify", params);
};

export const resetPasswordService = (data) => {
  const params = new URLSearchParams();
  for (const key in data) params.append(key, data[key]);
  return request.post("/user/forgot/reset", params);
};
