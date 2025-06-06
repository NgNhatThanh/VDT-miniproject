package org.vdt.qlch.meetinghistoryservice.dto;

public record ConnectionInfo(
        String sessionId,
        String userId,
        int meetingId
) {
}
