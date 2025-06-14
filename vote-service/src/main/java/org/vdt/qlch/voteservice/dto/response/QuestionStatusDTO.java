package org.vdt.qlch.voteservice.dto.response;

import java.io.Serializable;
import java.util.List;

public record QuestionStatusDTO(
        int questionId,
        List<OptionStatusDTO> options
) implements Serializable {
}
