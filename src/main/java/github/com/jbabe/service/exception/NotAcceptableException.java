package github.com.jbabe.service.exception;

import lombok.Getter;

@Getter
public class NotAcceptableException extends RuntimeException{
    private String detailMessage;
    private Object request;

    public NotAcceptableException(String detailMessage, Object request) {
        this.detailMessage = detailMessage;
        this.request = request;
    }
}
