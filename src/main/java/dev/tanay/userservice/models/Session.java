package dev.tanay.userservice.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Session{
    @Id
    private UUID sessionId;
    private Instant expiryAt;
    @Enumerated(EnumType.STRING)
    private SessionStatus status;
    @ManyToOne
    private User user;
}
