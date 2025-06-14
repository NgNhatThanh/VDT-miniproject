package org.vdt.qlch.voteservice.dto.response;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record VoteStatusDTO(
        int meetingVoteId,
        List<QuestionStatusDTO> questions
) implements Serializable {
}
