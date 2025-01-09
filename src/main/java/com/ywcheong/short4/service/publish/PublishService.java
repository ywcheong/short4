package com.ywcheong.short4.service.publish;

import com.ywcheong.short4.data.dto.ActivateRequestDTO;
import com.ywcheong.short4.data.dto.ActivateResult;
import com.ywcheong.short4.data.dto.PublishRequestDTO;

public interface PublishService {
    String publishURL(PublishRequestDTO shortURL);
    ActivateResult activateURL(ActivateRequestDTO requestDTO);

    String createManageSecret();
}
