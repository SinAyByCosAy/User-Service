package dev.tanay.userservice.repositories;

import dev.tanay.userservice.models.JwtKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtKeyRepository extends JpaRepository<JwtKeyEntity, Long> {
    JwtKeyEntity findByActiveTrue();
}
