package core.config;


import core.entities.User;
import core.enums.UserRole;
import core.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    @Bean
    ApplicationRunner applicationRunner(){
        return args -> {

            if(!userRepository.existsByRole(UserRole.ADMIN)){
                User user = User.builder()
                        .email("admin@example.com")
                        .username("admin")
                        .password(passwordEncoder.encode("Admin@123"))
                        .role(UserRole.ADMIN)
                        .build();
                userRepository.save(user);
                log.warn("Admin user has been created with default password: admin, please change it");
            }
            log.info("Application initialization completed .....");
        };
    }

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
