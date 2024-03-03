package github.com.jbabe.config.security;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class jasyptConfig {
    @Value("${jasypt.encryptor.password}")
    private String PASSWORD_KEY;

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor(){
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(PASSWORD_KEY); // 암호화에 사용하는 키
        config.setPoolSize("1"); //인스턴스 pool
        config.setAlgorithm("PBEWithMD5AndDES"); //알고리즘
        config.setStringOutputType("base64"); // 인코딩
        config.setKeyObtentionIterations("1000");  // 반복할 해싱 횟수
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");  // salt 생성 클래스 지정
        encryptor.setConfig(config); // 설정 정보 set
        return encryptor;
    }
}
