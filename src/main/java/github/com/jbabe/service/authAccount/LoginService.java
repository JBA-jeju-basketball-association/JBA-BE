package github.com.jbabe.service.authAccount;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {


    public String login(String email, String password) {


        return "Login Success";
    }
}
