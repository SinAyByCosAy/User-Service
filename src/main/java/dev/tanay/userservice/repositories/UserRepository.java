package dev.tanay.userservice.repositories;

import dev.tanay.userservice.dtos.UserDto;
import dev.tanay.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    public UserDto createUser(User user);
    public User findUserByEmail(String email);
    public User findUserById(Long id);
}
