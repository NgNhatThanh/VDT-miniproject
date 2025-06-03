package org.vdt.qlch.meetingservice.dto.response;

public record UserDTO(
        String id,
        String firstName,
        String lastName,
        String email,
        String picture
) {
}
