package github.com.jbabe.service.exception;

import lombok.Getter;

@Getter
public class NotAcceptableException extends RuntimeException{
    private String detailMessage;
    private String request;

    public NotAcceptableException(String detailMessage, String request) {
        this.detailMessage = detailMessage;
        this.request = request;
    }
}
