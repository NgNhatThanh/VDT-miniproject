package org.vdt.qlch.meetingservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JoinDTO(
        @NotBlank String userId,
        @NotNull Integer roleId
) {
}
