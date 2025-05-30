package org.vdt.qlch.userservice.dto;

import lombok.Builder;

@Builder
public record UserDTO(
        String username,
        String firstName,
        String lastName,
        String email,
        String picture
) {
}
