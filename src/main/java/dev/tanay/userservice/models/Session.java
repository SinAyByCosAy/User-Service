package dev.tanay.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class Session extends BaseModel{
    private String token;
    private Instant expiryAt;
    @Enumerated(EnumType.STRING)
    private SessionStatus status;
    @ManyToOne
    private User user;
}
