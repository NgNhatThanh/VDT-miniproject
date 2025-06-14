package org.vdt.qlch.voteservice.dto.response;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record OptionDTO(
        int id,
        String content
) implements Serializable {
}
