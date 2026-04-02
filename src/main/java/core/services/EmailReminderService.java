package core.services;


import java.util.Map;

public interface EmailReminderService {
    void sendBookingReminder(String customerEmail, String customerName, Map<String, Object> params);
}
