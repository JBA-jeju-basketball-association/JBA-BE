package github.com.jbabe.service.exception;

import lombok.Getter;

@Getter
public class InvalidReqeustException extends RuntimeException{
    private String detailMessage;
    private Object request;

    public InvalidReqeustException(String detailMessage, Object request) {
        this.detailMessage = detailMessage;
        this.request = request;
    }
}
