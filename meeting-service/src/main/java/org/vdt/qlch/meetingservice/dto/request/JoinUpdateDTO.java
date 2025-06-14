package org.vdt.qlch.meetingservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JoinUpdateDTO(
        @NotNull Integer joinId,
        @NotBlank String status,
        String reason
) {
}
