package com.ywcheong.short4.service.publish;

import com.ywcheong.short4.data.dto.ActivateRequestDTO;
import com.ywcheong.short4.data.dto.ActivateResult;
import com.ywcheong.short4.data.dto.PublishRequestDTO;
import com.ywcheong.short4.data.entity.ShortURL;

public interface PublishService {
    ShortURL publishURL(PublishRequestDTO shortURL);
    ActivateResult activateURL(ActivateRequestDTO requestDTO);
    String generateRandomManageSecret();
}
