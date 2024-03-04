package github.com.jbabe.service.exception;

import lombok.Getter;

@Getter
public class CustomBadCredentialsException extends RuntimeException{
    private final String detailMessage;
    private final String request;

    public CustomBadCredentialsException(String detailMessage, String request) {
        this.detailMessage = detailMessage;
        this.request = request;
    }
}

