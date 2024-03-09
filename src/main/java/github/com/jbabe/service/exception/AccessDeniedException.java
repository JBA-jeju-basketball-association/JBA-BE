package github.com.jbabe.service.exception;

import lombok.Getter;

@Getter
public class AccessDeniedException extends RuntimeException{
    private String detailMessage;
    private String request;

    public AccessDeniedException(String detailMessage, String request) {
        this.detailMessage = detailMessage;
        this.request = request;
    }
}
