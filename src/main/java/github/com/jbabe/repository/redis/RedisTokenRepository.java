package github.com.jbabe.repository.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;

@Repository
public class RedisTokenRepository {
    private final ValueOperations<String,Set<String>> logoutTokenValueOperation;

    public RedisTokenRepository(RedisTemplate<String,Set<String>> bean) {
        this.logoutTokenValueOperation = bean.opsForValue();
    }

    public void addBlacklistToken(String email, Set<String> accessAndRefreshToken, Duration exp){
        Set<String> oldValue = logoutTokenValueOperation.get(email);
        if (oldValue!=null) oldValue.addAll(accessAndRefreshToken);
        else oldValue = accessAndRefreshToken;
        logoutTokenValueOperation.set(email, oldValue, exp);
    }
    public Set<String> getBlacklist(String email){
        Set<String> blacklist = logoutTokenValueOperation.get(email);
        return blacklist != null ? blacklist : Collections.emptySet();
    }

}
