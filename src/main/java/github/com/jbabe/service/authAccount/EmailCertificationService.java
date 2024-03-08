package github.com.jbabe.service.authAccount;

import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.ConflictException;
import github.com.jbabe.service.exception.NotAcceptableException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static github.com.jbabe.config.certification.EmailCertificationConfig.generateRandomNumber;

@Service
@RequiredArgsConstructor
public class EmailCertificationService {
    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;
    private final UserJpa userJpa;
    private final int authNumber = generateRandomNumber(100000, 999999);
    @Value("${email.address}")
    private String emailAddress;

    public void joinEmail(String email) {
        if (userJpa.existsByEmail(email)) {
            throw new ConflictException("이미 가입된 이메일입니다.", email);
        }

        String setFrom = emailAddress; // email-config에 설정한 자신의 이메일 주소를 입력
        String toMail = email;
        String title = "[인증]제주특별자치도농구협회 회원 가입"; // 이메일 제목
        String content =
                "회원가입 창으로 돌아가 인증 번호를 정확히 입력해주세요." + 	//html 형식으로 작성 !
                        "<br><br>" +
                        "<h1>[인증 번호] : " + authNumber + "</h1>" ; //이메일 내용 삽입
        try {
            mailSend(setFrom, toMail, title, content);
        }catch (Exception e) {
            e.printStackTrace();
            throw new NotAcceptableException("메일을 발송할 수 없습니다.", email);
        }
    }

    @Transactional
    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();//JavaMailSender 객체를 사용하여 MimeMessage 객체를 생성
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");//이메일 메시지와 관련된 설정을 수행합니다.
            // true를 전달하여 multipart 형식의 메시지를 지원하고, "utf-8"을 전달하여 문자 인코딩을 설정
            helper.setFrom(setFrom);//이메일의 발신자 주소 설정
            helper.setTo(toMail);//이메일의 수신자 주소 설정
            helper.setSubject(title);//이메일의 제목을 설정
            helper.setText(content,true);//이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.
            mailSender.send(message);
            redisUtil.setDataExpire(Integer.toString(authNumber), toMail, 60*5L); // redis에 데이터 저장 // 유효기간 5분
        } catch (MessagingException e) {//이메일 서버에 연결할 수 없거나, 잘못된 이메일 주소를 사용하거나, 인증 오류가 발생하는 등 오류
            // 이러한 경우 MessagingException이 발생
            e.printStackTrace();//e.printStackTrace()는 예외를 기본 오류 스트림에 출력하는 메서드
        }
    }

    public Boolean checkAuthNum(String email, String authNum) {
        if (redisUtil.getData(authNum) == null) return false;
        else if (redisUtil.getData(authNum).equals(email)) return true;
        else return false;
    }
}
