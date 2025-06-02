package org.vdt.qlch.meetingservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CreateMeetingDTO(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime,
        @NotNull Integer locationId,
        @NotEmpty List<@Valid JoinDTO> joins,
        List<Integer> documentsIds
        ) {
}