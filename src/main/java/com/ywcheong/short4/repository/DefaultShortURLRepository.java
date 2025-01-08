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
            log.error("ShortURL Repository -> MongoDB :: reserve not acknowledged :: token [{}] result [{}]", token, result);
            throw new RuntimeException("reserve not acknowledged");
        }

        if (result.getModifiedCount() != 1) {
            log.info("ShortURL Repository -> MongoDB :: reserve failed since existing token :: token [{}] result [{}]", token, result);
            return false;
        }

        log.info("ShortURL Repository -> MongoDB :: reserve success :: shortURL [{}]", token);
        return true;
    }

    @Override
    public ShortURL publish(ShortURL shortURL) {
        Query queryStatement = new Query();
        queryStatement.addCriteria(Criteria.where("token").is(shortURL.getToken()));

        Update updateDefinition = new Update();
        updateDefinition.set("token", shortURL.getToken())
                .set("originalURL", shortURL.getOriginalURL())
                .set("expireAfterSeconds", shortURL.getExpireAfterSeconds())
                .set("expireAfterVisits", shortURL.getExpireAfterVisits())
                .set("isActivated", shortURL.getIsActivated())
                .set("isForcefullyDowned", shortURL.getIsForcefullyDowned())
                .set("accessSecretHash", shortURL.getAccessSecretHash())
                .set("manageSecretHash", shortURL.getManageSecretHash())
                .currentTimestamp("createdAt");

        UpdateResult result = mongoTemplate.updateFirst(queryStatement, updateDefinition, ShortURL.class);

        if (!result.wasAcknowledged()) {
            log.error("ShortURL Repository -> MongoDB :: publish not acknowledged :: shortURL [{}] result [{}]", shortURL, result);
            throw new RuntimeException("publish not acknowledged");
        }

        if (result.getModifiedCount() != 1) {
            log.error("ShortURL Repository -> MongoDB :: publish modified count not 1 :: shortURL [{}] result [{}]", shortURL, result);
            throw new RuntimeException("publish modified count not 1");
        }

        log.info("ShortURL Repository -> MongoDB :: publish acknowledged :: shortURL [{}]", shortURL);
        return shortURL;
    }

    @Override
    public boolean activate(String token, String manageSecretHash) {
        return false;
    }
}
