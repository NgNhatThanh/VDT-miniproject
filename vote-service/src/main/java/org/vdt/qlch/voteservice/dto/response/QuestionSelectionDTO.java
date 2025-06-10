package org.vdt.qlch.voteservice.dto.response;

import java.io.Serializable;

public record QuestionSelectionDTO(
        int questionId,
        int optionId
) implements Serializable {
}