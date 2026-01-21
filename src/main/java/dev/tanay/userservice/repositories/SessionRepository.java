package dev.tanay.userservice.repositories;

import dev.tanay.userservice.models.Session;
import dev.tanay.userservice.models.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findSessionByToken(String token);
    @Modifying
    @Query("""
            update Session s set s.status = :status where s.token = :token
                        """)
    int updateStatus(String token, SessionStatus status);
}
