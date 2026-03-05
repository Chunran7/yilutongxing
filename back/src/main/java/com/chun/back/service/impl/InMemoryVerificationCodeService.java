package com.chun.back.service.impl;

import com.chun.back.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryVerificationCodeService implements VerificationCodeService {

    private static class Entry {
        String code;
        long expireAt;
    }

    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    @Value("${app.dev-mode:true}")
    private boolean devMode;

    // 验证码有效期秒数
    @Value("${app.verify-code-ttl:300}")
    private long ttl;

    private final Random random = new Random();

    @Override
    public String sendCode(String email) {
        if (email == null || email.isBlank())
            return null;
        String code = String.format("%06d", random.nextInt(1_000_000));
        Entry e = new Entry();
        e.code = code;
        e.expireAt = Instant.now().getEpochSecond() + ttl;
        store.put(email, e);

        // TODO: 这里应调用邮件发送服务将 code 发送到邮箱。
        if (devMode) {
            // 开发模式下直接返回验证码，前端会提示这是开发者模式
            return code;
        }
        return null;
    }

    @Override
    public boolean verifyCode(String email, String code) {
        if (email == null || code == null)
            return false;
        Entry e = store.get(email);
        if (e == null)
            return false;
        if (Instant.now().getEpochSecond() > e.expireAt) {
            store.remove(email);
            return false;
        }
        return e.code.equals(code);
    }

    @Override
    public void removeCode(String email) {
        if (email != null)
            store.remove(email);
    }
}
