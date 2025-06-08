package org.vdt.qlch.voteservice.dto;

import jakarta.validation.constraints.NotBlank;

public record VoteOptionDTO(@NotBlank String content) {
}
