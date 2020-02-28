package cust.aowei.jwtstudy;

import cust.aowei.jwtstudy.util.JwtUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JwtStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtStudyApplication.class, args);
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils();
    }
}
