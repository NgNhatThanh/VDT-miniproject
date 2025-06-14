package org.vdt.qlch.meetingservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateNoteDTO(
        @NotNull Integer noteId,
        @NotBlank String content
) {
}
