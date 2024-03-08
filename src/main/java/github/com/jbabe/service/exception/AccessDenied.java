package github.com.jbabe.service.exception;

import lombok.Getter;

@Getter
public class AccessDenied extends RuntimeException{
    private String detailMessage;
    private String request;

    public AccessDenied(String detailMessage, String request) {
        this.detailMessage = detailMessage;
        this.request = request;
    }
}
