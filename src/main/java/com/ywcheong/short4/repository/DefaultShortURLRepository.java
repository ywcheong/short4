package com.ywcheong.short4.repository;

import com.mongodb.client.result.UpdateResult;
import com.ywcheong.short4.data.entity.ShortURL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class DefaultShortURLRepository implements ShortURLRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public DefaultShortURLRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean attemptReserve(String token) {
        // 1. shortURL이 DB에 존재 -> 아무것도 하지 않고 false를 반환한다.
        // 2. shortURL이 DB에 부재 -> {shortURL} 삽입 후 true를 반환한다.

        Query queryStatement = new Query();
        queryStatement.addCriteria(Criteria.where("token").is(token));

        Update updateStatement = new Update();
        updateStatement.setOnInsert("shortURL", token);

        UpdateResult result = mongoTemplate.upsert(queryStatement, updateStatement, ShortURL.class);

        if (!result.wasAcknowledged()) {
            log.error("reserve request [{}] not acknowledged", token);
            return false;
        }

        log.info("reserve request [{}] resulted in [{}]", token, result);
        return result.getModifiedCount() == 1;
    }

    @Override
    public boolean publish(ShortURL shortURL) {
        Query queryStatement = new Query();
        queryStatement.addCriteria(Criteria.where("token").is(shortURL.getToken()));

        Update updateDefinition = new Update();
        updateDefinition.set("token", shortURL.getToken())
                .set("originalURL", shortURL.getOriginalURL())
                .set("expireAfterSeconds", shortURL.getExpireAfterSeconds())
                .set("expireAfterVisits", shortURL.getExpireAfterVisits())
                .set("isActivated", shortURL.getIsActivated())
                .set("isForcefullyDowned", shortURL.getIsForcefullyDowned())
                .currentTimestamp("createdAt");

        if (shortURL.getAccessSecretHash() != null) {
            updateDefinition.set("accessSecretHash", shortURL.getAccessSecretHash());
        }

        if (shortURL.getManageSecretHash() != null) {
            updateDefinition.set("manageSecretHash", shortURL.getManageSecretHash());
        }

        UpdateResult result = mongoTemplate.updateFirst(queryStatement, updateDefinition, ShortURL.class);

        if (!result.wasAcknowledged()) {
            log.error("publish request [{}] not acknowledged", shortURL);
            return false;
        }

        log.info("publish request [{}] resulted in [{}]", shortURL, result);
        return result.getModifiedCount() == 1;
    }

    @Override
    public boolean activate(String token, String manageSecretHash) {
        return false;
    }
}
