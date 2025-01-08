package com.ywcheong.short4.validator;

import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Component
@Slf4j
public class HTTPKindURLValidator implements ConstraintValidator<HTTPKindURL, String> {
    @Value("${short4.server.allowed-protocols}")
    private List<String> allowedProtocols;

    @PostConstruct
    public void logAllowedProtocols() {
        log.info("HTTP-kind URL Validator :: allowed protocol list :: [{}]", allowedProtocols);
    }

    @Override
    public boolean isValid(String givenURL, ConstraintValidatorContext constraintValidatorContext) {
        try {
            String protocol = new URL(givenURL).getProtocol().toLowerCase();
            log.debug("HTTP-kind URL Validator :: protocol given :: protocol [{}] isvalid [{}]", protocol, allowedProtocols.contains(protocol));
            return allowedProtocols.contains(protocol);
        } catch (MalformedURLException e) {
            log.debug("HTTP-kind URL Validator :: MalformedURLException :: error [{}]", e.toString());
            return false;
        }
    }
}
