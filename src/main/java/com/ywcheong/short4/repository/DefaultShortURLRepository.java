package com.ywcheong.short4.repository;

import com.mongodb.client.result.UpdateResult;
import com.ywcheong.short4.data.entity.ShortURL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultShortURLRepository implements ShortURLRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public DefaultShortURLRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean attemptReserve(String shortUrl) {
        // 1. shortURL이 DB에 존재 -> 아무것도 하지 않고 false를 반환한다.
        // 2. shortURL이 DB에 부재 -> {shortURL} 삽입 후 true를 반환한다.

        Query queryStatement = new Query();
        queryStatement.addCriteria(Criteria.where("shortURL").is(shortUrl));

        Update updateStatement = new Update();
        updateStatement.setOnInsert("shortURL", shortUrl);

        UpdateResult result = mongoTemplate.upsert(
                queryStatement,
                updateStatement,
                ShortURL.class
        );

        return result.getModifiedCount() == 1;
    }
}
