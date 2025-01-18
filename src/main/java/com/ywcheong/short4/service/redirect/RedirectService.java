package com.ywcheong.short4.service.redirect;

import com.ywcheong.short4.data.dto.redirect.RedirectResult;

public interface RedirectService {
    RedirectResult redirect(String token, String accessSecret);
}
