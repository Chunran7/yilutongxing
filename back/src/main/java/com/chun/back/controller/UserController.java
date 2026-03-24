// package com.chun.back.controller;

// import com.chun.back.pojo.Result;
// import com.chun.back.pojo.User;
// import com.chun.back.service.UserService;
// import com.chun.back.utils.JwtUtil;
// import com.chun.back.utils.Md5Util;
// import jakarta.servlet.http.HttpServletRequest;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;
// import java.util.HashMap;
// import java.util.Map;

// @RestController
// @RequestMapping("/user")
// public class UserController {

// @Autowired
// private UserService userService;

// @Autowired
// private com.chun.back.service.VerificationCodeService
// verificationCodeService;

// private Long getLoginUserId(HttpServletRequest request) {
// Object obj = request.getAttribute("claims");
// if (obj instanceof Map<?, ?> m) {
// Object id = m.get("id");
// if (id != null)
// return Long.valueOf(id.toString());
// }
// return null;
// }

// private Long tryGetViewerId(HttpServletRequest request) {
// String auth = request.getHeader("Authorization");
// if (auth == null || auth.isBlank())
// return null;
// String token = auth.startsWith("Bearer ") ? auth.substring(7) : auth;
// try {
// Map<String, Object> claims = JwtUtil.parseToken(token);
// Object id = claims.get("id");
// return id == null ? null : Long.valueOf(id.toString());
// } catch (Exception e) {
// return null;
// }
// }

// /**
// * 忘记密码：发送验证码到邮箱（开发模式会在响应中返回验证码以便调试）
// * 前端应传递 form.x-www-form-urlencoded 或 JSON： { email }
// */
// @PostMapping("/forgot/send")
// public Result sendForgotCode(@RequestParam String email) {
// if (email == null || email.isBlank())
// return Result.error("邮箱不能为空");
// String code = verificationCodeService.sendCode(email.trim());
// Map<String, Object> res = new HashMap<>();
// if (code != null) {
// res.put("devMode", true);
// res.put("code", code);
// res.put("message", "开发者模式：验证码已返回给前端，生产环境会发送邮件");
// } else {
// res.put("devMode", false);
// res.put("message", "如果该邮箱已注册，验证码会发送至邮箱。");
// }
// return Result.success(res);
// }

// @PostMapping("/forgot/verify")
// public Result verifyForgotCode(@RequestParam String email, @RequestParam
// String code) {
// if (email == null || email.isBlank() || code == null || code.isBlank())
// return Result.error("邮箱和验证码不能为空");
// boolean ok = verificationCodeService.verifyCode(email.trim(), code.trim());
// if (!ok)
// return Result.error("验证码错误或已过期");
// return Result.success();
// }

// @PostMapping("/register")
// public Result register(@RequestParam String username, @RequestParam String
// password) {
// // 基础校验：避免空参导致数据库异常/脏数据
// if (username == null || username.isBlank()) {
// return Result.error("用户名不能为空");
// }
// if (password == null || password.isBlank()) {
// return Result.error("密码不能为空");
// }
// username = username.trim();

// User exist = userService.findByUserName(username);
// if (exist != null) {
// return Result.error("用户名已存在");
// }
// // register 内部会对插入结果进行判断，失败会抛出异常并由全局异常处理器返回 msg
// userService.register(username, password);
// return Result.success();
// }

// @PostMapping("/login")
// public Result login(@RequestParam String username, @RequestParam String
// password) {
// User u = userService.findByUserName(username);
// if (u == null) {
// return Result.error("用户名不存在");
// }
// String md5 = Md5Util.getMD5String(password);
// if (u.getPasswordHash() == null || !u.getPasswordHash().equals(md5)) {
// return Result.error("密码错误");
// }
// if (u.getStatus() != null && u.getStatus() == 0) {
// return Result.error("账号已被封禁");
// }

// Map<String, Object> claims = new HashMap<>();
// claims.put("id", u.getId());
// claims.put("username", u.getUsername());
// String token = JwtUtil.genToken(claims);
// return Result.success(token);
// }

// @GetMapping("/me")
// public Result me(HttpServletRequest request) {
// Long userId = getLoginUserId(request);
// User me = userService.getMe(userId);
// if (me == null)
// return Result.error("用户不存在");
// return Result.success(me);
// }

// @GetMapping("/profile/{id}")
// public Result profile(@PathVariable Long id, HttpServletRequest request) {
// Long viewerId = tryGetViewerId(request);
// User profile = userService.getProfile(id, viewerId);
// if (profile == null)
// return Result.error("用户不存在");
// return Result.success(profile);
// }

// @PutMapping("/profile")
// public Result updateProfile(@RequestBody Map<String, String> body,
// HttpServletRequest request) {
// Long userId = getLoginUserId(request);
// String nickname = body.get("nickname");
// String email = body.get("email");
// String userPic = body.get("userPic");
// userService.updateProfile(userId, nickname, email, userPic);
// return Result.success();
// }
// }
