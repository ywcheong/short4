package com.ywcheong.short4.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Component
public class HTTPKindURLValidator implements ConstraintValidator<HTTPKindURL, String> {
    @Value("${short4.server.allowed-protocols}")
    private List<String> allowedProtocols;

    @Override
    public boolean isValid(String givenURL, ConstraintValidatorContext constraintValidatorContext) {
        try {
            String protocol = new URL(givenURL).getProtocol();
            return allowedProtocols.contains(protocol);
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
