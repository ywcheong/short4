package com.ywcheong.short4.repository;

import com.mongodb.client.result.UpdateResult;
import com.ywcheong.short4.data.entity.ShortURL;
import com.ywcheong.short4.data.types.ActivateResultType;
import com.ywcheong.short4.exception.MongoFailureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class DefaultShortURLRepository implements ShortURLRepository {
    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DefaultShortURLRepository(MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder) {
        this.mongoTemplate = mongoTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean tryReserveThenResult(String token) {
        // 1. shortURL이 DB에 존재 -> 아무것도 하지 않고 false를 반환한다.
        // 2. shortURL이 DB에 부재 -> {shortURL} 삽입 후 true를 반환한다.

        Query queryStatement = new Query();
        queryStatement.addCriteria(Criteria.where("token").is(token));

        Update updateStatement = new Update();
        updateStatement.setOnInsert("token", token);

        UpdateResult result = mongoTemplate.upsert(queryStatement, updateStatement, ShortURL.class);

        if (!result.wasAcknowledged()) {
            log.error("ShortURL Repository :: reserve not acknowledged :: token [{}] result [{}]", token, result);
            throw new RuntimeException("reserve not acknowledged");
        }

        if (result.getMatchedCount() == 1) {
            log.info("ShortURL Repository :: reserve failed since existing token :: token [{}] result [{}]", token, result);
            return false;
        }

        log.info("ShortURL Repository :: reserve success :: shortURL [{}]", token);
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
                .currentDate("createdAt")
                .currentDate("modifiedAt");

        UpdateResult result = mongoTemplate.updateFirst(queryStatement, updateDefinition, ShortURL.class);

        if (!result.wasAcknowledged()) {
            log.error("ShortURL Repository :: publish not acknowledged :: shortURL [{}] result [{}]", shortURL, result);
            throw new MongoFailureException("publish not acknowledged");
        }

        if (result.getModifiedCount() != 1) {
            log.error("ShortURL Repository :: publish modified count not 1 :: shortURL [{}] result [{}]", shortURL, result);
            throw new MongoFailureException("publish modified count not 1");
        }

        log.info("ShortURL Repository :: publish acknowledged :: shortURL [{}]", shortURL);
        return shortURL;
    }

    @Override
    public ActivateResultType activate(String token, String manageSecretHash) {
        return activate(token, manageSecretHash, 2);
    }

    public ActivateResultType activate(String token, String manageSecretHash, int recursionDepth) {
        if (recursionDepth <= 0) {
            log.error("ShortURL Repository :: activate recursion unexpectedly too deep :: token [{}]", token);
            throw new RuntimeException("activate recursion unexpectedly too deep");
        }

        Query findingQueryStatement = new Query();
        findingQueryStatement.addCriteria(Criteria.where("token").is(token));

        ShortURL foundShortURL = mongoTemplate.findOne(findingQueryStatement, ShortURL.class);

        // 그런 토큰 없습니다 or 아직 예약(attemptReserve)만 된 토큰입니다 :(
        if (foundShortURL == null || foundShortURL.getIsActivated() == null) {
            log.info("ShortURL Repository :: activate attempt but token not found :: token [{}]", token);
            return ActivateResultType.TOKEN_NOT_FOUND;
        }

        // 관리 비밀번호를 틀린 당신에게 해 줄 말이 없습니다 :(
        String storedManageSecretHash = foundShortURL.getManageSecretHash();
        if (!passwordEncoder.matches(manageSecretHash, storedManageSecretHash)) {
            log.info("ShortURL Repository :: activate attempt but wrong manage secret :: token [{}]", token);
            return ActivateResultType.WRONG_MANAGE_SECRET;
        }

        // 관리 비밀번호는 맞췄는데, 이미 활성화되어 있습니다 :(
        if (foundShortURL.getIsActivated()) {
            log.info("ShortURL Repository :: activate attempt but already activated :: token [{}]", token);
            return ActivateResultType.ALREADY_ACTIVATED;
        }

        // 관리 비밀번호도 맞췄고, 활성화가 안 되어 있으니 활성화를 시도합니다...
        log.info("ShortURL Repository :: entity found so try updating :: token [{}]", token);
        Query updatingQueryStatement = new Query();
        updatingQueryStatement.addCriteria(Criteria.where("token").is(token));
        updatingQueryStatement.addCriteria(Criteria.where("isActivated").is(false));

        Update updateDefinition = new Update();
        updateDefinition.set("isActivated", true);

        UpdateResult result = mongoTemplate.updateFirst(updatingQueryStatement, updateDefinition, ShortURL.class);

        if (!result.wasAcknowledged()) {
            log.error("ShortURL Repository :: activate not acknowledged :: token [{}] result [{}]", token, result);
            throw new RuntimeException("activate not acknowledged");
        }

        if (result.getModifiedCount() != 1) {
            // 활성화 시도했는데, 그새 race condition이 터져서 누가 활성화했거나 삭제당했습니다.
            // 어떤 race condition인지 파악하기 위해 재귀호출을 시행합니다.
            log.error("ShortURL Repository :: activate might race condition so retry:: token [{}] result [{}]", token, result);
            return activate(token, manageSecretHash, recursionDepth - 1);
        }

        // 활성화 성공했습니다.
        log.error("ShortURL Repository :: activate acknowledged :: token [{}] result [{}]", token, result);
        return ActivateResultType.SUCCESS;
    }
}
