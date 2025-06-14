package org.vdt.commonlib.dto;

import lombok.Builder;
import org.vdt.commonlib.model.MeetingHistoryType;

@Builder
public record MeetingHistoryMessage(
        int meetingId,
        String content,
        MeetingHistoryType type
) {
}
