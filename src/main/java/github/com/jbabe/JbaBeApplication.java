package github.com.jbabe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class JbaBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(JbaBeApplication.class, args);
    }

}
