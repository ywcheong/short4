package com.ywcheong.short4.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "short4.stat")
@Getter
@Setter
public class Short4Properties {
    private int expireAfterHours;
}
