package org.vdt.commonlib.dto;

import lombok.Builder;

@Builder
public record UserDTO(
        String id,
        String username,
        String firstName,
        String lastName,
        String email,
        String picture
) {
}
