package dev.tanay.userservice.dtos;

import dev.tanay.userservice.models.Role;
import dev.tanay.userservice.models.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class UserDto {
    private String email;
    private Set<String> roles = new HashSet<>();
    public static UserDto from(User user){
        if(user == null) return null;

        return UserDto.builder()
                .email(user.getEmail())
                .roles(user.getRoles() == null ? new HashSet<>() :
                    user.getRoles().stream()
                            .map(Role :: getName)
                            .collect(Collectors.toSet()))
                .build();
    }
}
