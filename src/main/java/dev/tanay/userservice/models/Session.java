package dev.tanay.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Session extends BaseModel{
    private String token;
    private Date expiryAt;
    private boolean isActive;
    @ManyToOne
    private User user;
}
