package org.vdt.qlch.speechservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateSpeechDTO(
        @NotNull Integer speechId,
        @NotBlank String status
) {
}