package dev.tanay.userservice.repositories;

import dev.tanay.userservice.models.Session;
import dev.tanay.userservice.models.SessionStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    Optional<Session> findSessionBySessionId(UUID sessionId);
    @Transactional
    @Modifying
    @Query("""
            update Session s set s.status = :status where s.sessionId = :sessionId
                        """)
    int updateStatus(UUID sessionId, SessionStatus status);

    @Query("""
        select max(s.expiryAt)
            from Session s
    """)
    Optional<Instant> findLatestSessionExpiry();
}
