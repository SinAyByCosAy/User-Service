package dev.tanay.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@Entity
@Getter
@Setter
public class Session extends BaseModel{
    private String token;
    private Instant expiryAt;
    private boolean isActive;
    @ManyToOne
    private User user;
}
