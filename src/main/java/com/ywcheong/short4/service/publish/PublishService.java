package com.ywcheong.short4.service.publish;

import com.ywcheong.short4.data.dto.PublishResponseDTO;
import com.ywcheong.short4.data.dto.PublishServiceDTO;

public interface PublishService {
    PublishResponseDTO publishURL(PublishServiceDTO dto);

    String generateRandomManageSecret();
}
