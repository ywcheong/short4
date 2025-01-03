package com.ywcheong.short4.data.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Builder
@ToString
@Document
public class ShortURL {
    @Id
    @Indexed(unique = true)
    private String token;
    private String originalURL;

    private int expireAfterSeconds;
    private int expireAfterVisits;

    private String salt;
    private String accessSecretHash;
    private String manageSecretHash;

    private Boolean isActivated;
    private Boolean isForcefullyDowned;

    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date modifiedAt;
}
