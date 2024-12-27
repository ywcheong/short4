package com.ywcheong.short4.utility;

import org.springframework.stereotype.Component;

@Component
public class RandomTokenGenerator {
    // Todo // 서버 부팅 시간에 따른 시드 의존성 주입
    private final int randomSeed = 1234;

    public String makeNewToken() {
        return "hello";
    }
}
