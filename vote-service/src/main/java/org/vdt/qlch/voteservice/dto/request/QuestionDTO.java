package org.vdt.qlch.voteservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record QuestionDTO(
        @NotBlank String title,
        @NotEmpty List<VoteOptionDTO> options
        ) {
}
