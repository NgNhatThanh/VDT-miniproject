package org.vdt.qlch.meetingservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateDocumentStatusDTO(
        @NotNull Integer meetingId,
        @NotNull Integer meetingDocumentId,
        @NotBlank String status
) {
}
