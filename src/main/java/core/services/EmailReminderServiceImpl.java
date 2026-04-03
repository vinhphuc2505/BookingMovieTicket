package core.services;


import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailReminderServiceImpl implements EmailReminderService{

    @Value("${brevo.api-key}")
    private String API_KEY;

    @Value("${brevo.url}")
    private String BREVO_API_URL;

    private final RestTemplate restTemplate;


    @Override
    @Transactional
    @Async("emailExecutor")
    public void sendBookingReminder(String customerEmail, String customerName, Map<String, Object> params) {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", API_KEY);

            // 2. Xây dựng Body theo cấu trúc Brevo yêu cầu
            Map<String, Object> body = new HashMap<>();
            body.put("sender", Map.of("email", "tranphuc250503@gmail.com", "name", "Booking ticket movie"));
            body.put("to", List.of(Map.of("email", customerEmail, "name", customerName)));
            body.put("templateId", 1);
            body.put("params", params);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // 3. Gọi API Brevo
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Send mail successfully {}", customerEmail);
            } else {
                log.error("Brevo response error: {} {}", response.getStatusCode(), customerEmail);
            }


        } catch (Exception e){
            log.error("Error: {}", e.getMessage());
            e.printStackTrace();
        }
    }





}






