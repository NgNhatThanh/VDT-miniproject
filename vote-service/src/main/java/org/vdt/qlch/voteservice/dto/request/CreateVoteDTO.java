package org.vdt.qlch.voteservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CreateVoteDTO(
        @NotNull Integer meetingId,
        @NotBlank String title,
        @NotNull String description,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime,
        @NotNull String type,
        @NotNull List<Integer> documentIds,
        @NotEmpty List<@Valid QuestionDTO> questions
        ) {
}
