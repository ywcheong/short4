package com.ywcheong.short4.data.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ActivateResult {
    public static enum ActivateResultType {
        SUCCESS,
        TOKEN_NOT_FOUND,
        WRONG_MANAGE_SECRET,
        ALREADY_ACTIVATED
    }

    private ActivateResultType result;
}
