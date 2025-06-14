package org.vdt.commonlib.dto;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record UserDTO(
        String id,
        String username,
        String fullName,
        String firstName,
        String lastName,
        String email,
        String picture
) implements Serializable {
}
