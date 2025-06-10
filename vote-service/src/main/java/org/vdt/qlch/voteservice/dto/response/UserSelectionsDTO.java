package org.vdt.qlch.voteservice.dto.response;

import java.io.Serializable;
import java.util.List;

public record UserSelectionsDTO(
        List<QuestionSelectionDTO> selections
) implements Serializable {
}
