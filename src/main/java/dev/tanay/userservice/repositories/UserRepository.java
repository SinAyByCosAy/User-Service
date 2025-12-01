package dev.tanay.userservice.repositories;

import dev.tanay.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public void createUser(User user);
    public String getPassword(String email);
    public void deleteUser(Long id);
}
