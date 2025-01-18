package com.ywcheong.short4.data.dto.publish;

public class PublishResult extends PublishResponse {
    // 단순하므로 변환 없이 그대로 사용
    public PublishResult(String token, String manageSecret) {
        super(token, manageSecret);
    }
}
