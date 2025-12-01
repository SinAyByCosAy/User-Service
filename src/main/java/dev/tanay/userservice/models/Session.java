package dev.tanay.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Session extends BaseModel{
    private String token;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
