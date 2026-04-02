package core.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "emailExecutor")
    public Executor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Số lượng thread chạy thường trực
        executor.setMaxPoolSize(10); // Số lượng thread tối đa khi hàng đợi đầy
        executor.setQueueCapacity(100); // Kích thước hàng đợi
        executor.setThreadNamePrefix("BrevoEmail-");
        executor.initialize();
        return executor;
    }
}
