package org.vdt.commonlib.dto;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record DocumentDTO(
        int id,
        String name,
        int size,
        String url
) implements Serializable {
}
