package org.vdt.qlch.voteservice.dto.response;

import lombok.Setter;
import org.vdt.qlch.voteservice.model.MeetingVoteType;

import java.io.Serializable;
import java.time.LocalDateTime;

public record MeetingVoteDTO(
        int id,
        String title,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        MeetingVoteType type,
        boolean isVoted) implements Serializable {
}
