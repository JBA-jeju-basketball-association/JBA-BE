package github.com.jbabe.service.exception;

import io.jsonwebtoken.JwtException;
import lombok.Getter;

@Getter
public class ExpiredJwtException extends JwtException {
    private String detailMessage;
    private Object request;

    public ExpiredJwtException(String detailMessage, Object request) {
        super(detailMessage);
        this.detailMessage = detailMessage;
        this.request = request;
    }

    public ExpiredJwtException(String detailMessage) {
        super(detailMessage);
        this.detailMessage = detailMessage;
    }
}
