package github.com.jbabe.service.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{
    private String detailMessage;
    private Object request;

    public NotFoundException(String detailMessage, Object request) {
        this.detailMessage = detailMessage;
        this.request = request;
    }
}
