package dev.tanay.userservice.MessageQueueConfig;

import dev.tanay.events.EmailMessage;
import dev.tanay.userservice.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventPublisher {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    private static final String TOPIC = "user-signup-topic";

    public void publishUserCreated(User user){
        EmailMessage msg = EmailMessage.newBuilder()
                        .setId(user.getId())
                        .setUsername(user.getEmail())
                        .setEmail(user.getEmail())
                        .build();

        kafkaTemplate.send(
                TOPIC,
                msg.getEmail(),
                msg.toByteArray()
        );
    }
}
