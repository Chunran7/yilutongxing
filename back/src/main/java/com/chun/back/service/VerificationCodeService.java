package com.chun.back.service;

public interface VerificationCodeService {
    /**
     * 发送验证码到指定邮箱（开发模式可直接返回验证码）
     * 
     * @param email 目标邮箱
     * @return 在开发模式返回验证码字符串，否则返回 null
     */
    String sendCode(String email);

    /** 校验邮箱对应的验证码是否匹配 */
    boolean verifyCode(String email, String code);

    /** 清理邮箱对应的验证码（例如成功重置后） */
    void removeCode(String email);
}
