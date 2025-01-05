package com.ywcheong.short4.service.publish;

import com.ywcheong.short4.data.entity.ShortURL;

public interface PublishService {
    boolean publishURL(ShortURL shortURL);
    String generateRandomManageSecret();
}
