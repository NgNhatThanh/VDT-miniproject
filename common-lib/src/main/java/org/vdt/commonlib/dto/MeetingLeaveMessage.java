package org.vdt.commonlib.dto;

public record MeetingLeaveMessage(
        String userId,
        int meetingId
) {
}
