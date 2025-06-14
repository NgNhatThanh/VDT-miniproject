package org.vdt.qlch.speechservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SpeechRegisterDTO(
        @NotNull Integer meetingId,
        @NotBlank String content,
        @NotNull @Min(1) @Max(30) Integer duration
) {
}
