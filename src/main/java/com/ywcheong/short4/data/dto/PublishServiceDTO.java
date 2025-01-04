package com.ywcheong.short4.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@Builder
@ToString
public class PublishServiceDTO {
    private String originalURL;

    private int expireAfterSeconds;
    private int expireAfterVisits;

    @ToString.Exclude
    private String accessSecretHash;
    @ToString.Exclude
    private String manageSecretHash;

    private Boolean isActivated;
    private Boolean isForcefullyDowned;

    private Date createdAt;
    private Date modifiedAt;
}
