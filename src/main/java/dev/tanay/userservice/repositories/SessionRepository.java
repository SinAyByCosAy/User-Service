package dev.tanay.userservice.repositories;

import dev.tanay.userservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
    public void deleteByToken(String token);
    public Session findSessionByToken(String token);
}
