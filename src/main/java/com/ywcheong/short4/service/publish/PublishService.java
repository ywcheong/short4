package com.ywcheong.short4.service.publish;

import com.ywcheong.short4.data.dto.publish.ActivateRequest;
import com.ywcheong.short4.data.dto.publish.ActivateResult;
import com.ywcheong.short4.data.dto.publish.PublishRequest;
import com.ywcheong.short4.data.dto.publish.PublishResult;

public interface PublishService {
    PublishResult publishURL(PublishRequest shortURL);

    ActivateResult activateURL(ActivateRequest requestDTO);

    String createRandomManageSecret();
}
