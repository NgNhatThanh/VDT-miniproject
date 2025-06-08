package org.vdt.qlch.voteservice.dto;

import java.time.LocalDateTime;

public record MeetingVoteDTO(
        String title,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        boolean isVoted) {
}
