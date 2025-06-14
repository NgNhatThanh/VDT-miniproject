package org.vdt.qlch.voteservice.dto.response;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record MeetingVoteStatusDTO(
        MeetingVoteDetailDTO detail,
        UserSelectionsDTO vote,
        VoteStatusDTO voteStatus,
        boolean voted
) implements Serializable {
}
