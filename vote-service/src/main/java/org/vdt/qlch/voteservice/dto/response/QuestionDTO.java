package org.vdt.qlch.voteservice.dto.response;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record QuestionDTO(
        int id,
        String title,
        List<OptionDTO> options
) implements Serializable {
}
