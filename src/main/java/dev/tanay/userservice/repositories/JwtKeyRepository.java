package dev.tanay.userservice.repositories;

import dev.tanay.userservice.models.JwtKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtKeyRepository extends JpaRepository<JwtKeyEntity, Long> {
    JwtKeyEntity findByActiveTrue();
    JwtKeyEntity findTopByActiveTrueOrderByCreatedAtDesc();
    Optional<JwtKeyEntity> findByKid(String kid);
}
