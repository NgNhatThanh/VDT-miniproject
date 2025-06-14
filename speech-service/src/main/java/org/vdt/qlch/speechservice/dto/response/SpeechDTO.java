package org.vdt.qlch.speechservice.dto.response;

import lombok.Builder;
import org.vdt.qlch.speechservice.model.SpeechStatus;

import java.time.LocalDateTime;

@Builder
public record SpeechDTO(
        int id,
        String content,
        int duration,
        SpeechStatus status,
        String speakerFullName,
        LocalDateTime createdAt
) {
}
