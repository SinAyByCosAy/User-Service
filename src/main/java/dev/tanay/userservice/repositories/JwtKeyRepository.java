package dev.tanay.userservice.repositories;

import dev.tanay.userservice.models.JwtKeyEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface JwtKeyRepository extends JpaRepository<JwtKeyEntity, Long> {
    JwtKeyEntity findByActiveTrue();
    JwtKeyEntity findTopByActiveTrueOrderByCreatedAtDesc();
    Optional<JwtKeyEntity> findByKid(String kid);

    @Modifying
    @Transactional
    @Query("""
        update JwtKeyEntity k
            set k.active = false,
                k.retiredAt = :retiredAt
            where k.active = true
    """)
    int retireKey(@Param("retiredAt") Instant retiredAt);
}
