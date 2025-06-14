package org.vdt.qlch.voteservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VoteOptionDTO(@NotBlank String content) {
}
