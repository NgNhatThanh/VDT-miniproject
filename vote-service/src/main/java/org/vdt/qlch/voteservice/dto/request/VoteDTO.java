package org.vdt.qlch.voteservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VoteDTO(
        @NotNull Integer meetingId,
        @NotNull Integer meetingVoteId,
        @NotEmpty List<@Valid QuestionSelectDTO> questionSelections
) {
}
