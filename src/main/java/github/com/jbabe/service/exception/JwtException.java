package github.com.jbabe.service.exception;

import lombok.Getter;

@Getter
public class JwtException extends io.jsonwebtoken.JwtException {
    private String detailMessage;
    private Object request;

    public JwtException(String detailMessage, Object request) {
        super(detailMessage);
        this.detailMessage = detailMessage;
        this.request = request;
    }

    public JwtException(String detailMessage) {
        super(detailMessage);
        this.detailMessage = detailMessage;
    }
}
