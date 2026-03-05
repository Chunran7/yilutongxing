# SRTP-Chen2025

## Authentication / 使用说明

该项目使用 JWT 做简单的鉴权机制：

- 登录接口：POST /user/login，参数 username 和 password，返回 Result.success(token)（data 为 token 字符串）。
- 客户端在后续需要鉴权的请求中在 HTTP Header 中加入：Authorization: Bearer <token>

示例（PowerShell + curl 或 Postman）：

1. 登录并获取 token

```powershell
# 返回的 JSON 中 data 字段为 token
curl -X POST "http://localhost:8080/user/login" -d "username=your&password=pass"
```

2. 发帖（需要登录）

```powershell
curl -X POST "http://localhost:8080/post" -H "Authorization: Bearer <token>" -H "Content-Type: application/json" -d '{"title":"标题","content":"内容"}'
```

说明：

- 未携带或 token 无效时，服务会返回 401 并且 body 为 {"code":1,"msg":"需要登录","data":null}。
- 发帖接口会自动从 token 中读取当前登录用户 id 并设置到帖子 `userId` 字段，无需客户端手动传入。
