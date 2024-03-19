package github.com.jbabe.repository.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Set;

@Repository
public class RedisTokenRepository {
    private final RedisTemplate<String,Set<String>> logoutTokenTemplate;
    private final ValueOperations<String,Set<String>> logoutTokenValueOperation;

    public RedisTokenRepository(RedisTemplate<String,Set<String>> bean) {
        this.logoutTokenTemplate = bean;
        this.logoutTokenValueOperation = logoutTokenTemplate.opsForValue();
    }

    public void addBlacklistToken(String email, Set<String> accessAndRefreshToken, Duration exp){
        Set<String> oldValue = logoutTokenValueOperation.get(email);
        if (oldValue!=null) oldValue.addAll(accessAndRefreshToken);
        else oldValue = accessAndRefreshToken;
        logoutTokenValueOperation.set(email, oldValue, exp);
    }
    public Set<String> getBlacklist(String email){
        return logoutTokenValueOperation.get(email);
    }
}
