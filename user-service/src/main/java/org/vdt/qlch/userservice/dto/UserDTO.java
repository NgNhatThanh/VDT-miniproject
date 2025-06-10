package org.vdt.qlch.userservice.dto;

import lombok.Builder;
import org.keycloak.representations.idm.UserRepresentation;

@Builder
public record UserDTO(
        String id,
        String username,
        String fullName,
        String firstName,
        String lastName,
        String email,
        String picture
) {

    public static UserDTO from(UserRepresentation user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getLastName() + " " + user.getFirstName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .picture(user.getAttributes().get("Picture").getFirst())
                .build();
    }

}
