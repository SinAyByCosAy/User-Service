package dev.tanay.userservice.repositories;

import dev.tanay.userservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findSessionByTokenAndIsActiveTrueAndExpiryAtAfter(String token, Instant now);
    void deleteSessionByToken(String token);
}
