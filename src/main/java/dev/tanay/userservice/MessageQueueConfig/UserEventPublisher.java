package dev.tanay.userservice.MessageQueueConfig;

import dev.tanay.userservice.dtos.EmailMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventPublisher {
    private final KafkaTemplate<String, EmailMessageDto> kafkaTemplate;
    private static final String TOPIC = "user-signup-topic";
    public void publishUserCreated(EmailMessageDto msg){
        kafkaTemplate.send(
                TOPIC,
                msg.getEmail(),
                msg
        );
    }
}
