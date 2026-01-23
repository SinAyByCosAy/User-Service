package dev.tanay.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class JwtKeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String secretBase64;
    String algorithm;
    boolean active;
}
