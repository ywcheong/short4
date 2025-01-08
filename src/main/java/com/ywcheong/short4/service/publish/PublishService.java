package com.ywcheong.short4.service.publish;

import com.ywcheong.short4.data.dto.PublishRequestDTO;
import com.ywcheong.short4.data.entity.ShortURL;

public interface PublishService {
    ShortURL publishURL(PublishRequestDTO shortURL);
    String generateRandomManageSecret();
}
