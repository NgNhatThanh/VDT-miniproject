package org.vdt.qlch.voteservice.dto.request;

import jakarta.validation.constraints.NotNull;

public record QuestionSelectDTO(
        @NotNull Integer questionId,
        @NotNull Integer optionId
) {
}
