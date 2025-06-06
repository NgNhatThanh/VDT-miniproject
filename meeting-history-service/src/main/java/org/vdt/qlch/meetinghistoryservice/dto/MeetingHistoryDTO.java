package org.vdt.qlch.meetinghistoryservice.dto;

import org.vdt.commonlib.model.MeetingHistoryType;
import org.vdt.qlch.meetinghistoryservice.model.MeetingHistory;

import java.time.LocalDateTime;

public record MeetingHistoryDTO(
        String content,
        MeetingHistoryType type,
        LocalDateTime createdAt) {

    public static final MeetingHistoryDTO from(MeetingHistory history){
        return new MeetingHistoryDTO(history.getContent(),
                history.getType(),
                history.getCreatedAt());
    }

}
