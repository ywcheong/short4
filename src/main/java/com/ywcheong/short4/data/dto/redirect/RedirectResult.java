package com.ywcheong.short4.data.dto.redirect;

import com.ywcheong.short4.data.types.RedirectResultType;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;

@Getter
@Setter
public class RedirectResult {
    RedirectResultType resultType;
    URI originalURI;

    public RedirectResult(RedirectResultType resultType) {
        this(resultType, null);
    }

    public RedirectResult(RedirectResultType resultType, URI originalURI) {
        this.resultType = resultType;
        this.originalURI = originalURI;
    }
}
