package org.vdt.commonlib.dto;

import lombok.Builder;
import org.vdt.commonlib.model.MeetingHistoryType;

import java.io.Serializable;

@Builder
public record MeetingHistoryMessage(
        int meetingId,
        String content,
        MeetingHistoryType type
) implements Serializable {
}
