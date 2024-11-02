package github.com.jbabe.service.exception;

import github.com.jbabe.web.dto.authSocial.SocialAccountDto;
import lombok.Getter;

@Getter
public class NotFoundSocialAccount extends RuntimeException{
    private final SocialAccountDto request;
    private final String detailMessage;


    public NotFoundSocialAccount(SocialAccountDto request, String detailMessage) {
        this.request = request;
        this.detailMessage = detailMessage;
    }
}
