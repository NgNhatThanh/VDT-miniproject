package org.vdt.qlch.voteservice.dto.response;

import lombok.Builder;
import org.vdt.commonlib.dto.DocumentDTO;
import org.vdt.qlch.voteservice.model.MeetingVoteType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MeetingVoteDetailDTO(
        int id,
        String title,
        String description,
        MeetingVoteType type,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<DocumentDTO> documents,
        List<QuestionDTO> questions
) implements Serializable {
}
