package com.ywcheong.short4.service.publish;

import com.ywcheong.short4.data.dto.PublishResponseDTO;
import com.ywcheong.short4.data.entity.ShortURL;

public interface PublishService {
    PublishResponseDTO publishURL(ShortURL shortURL);

    String generateRandomManageSecret();
}
