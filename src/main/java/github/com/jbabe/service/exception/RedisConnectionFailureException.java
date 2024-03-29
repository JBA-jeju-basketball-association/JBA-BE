package github.com.jbabe.service.exception;

import lombok.Getter;

@Getter
public class RedisConnectionFailureException extends RuntimeException{
    private String detailMessage;
    private Object request;

    public RedisConnectionFailureException(String detailMessage, Object request) {
        this.detailMessage = detailMessage;
        this.request = request;
    }
}
