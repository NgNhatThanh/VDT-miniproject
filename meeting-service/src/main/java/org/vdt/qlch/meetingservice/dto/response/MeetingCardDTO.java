package org.vdt.qlch.meetingservice.dto.response;

import java.sql.Timestamp;

public record MeetingCardDTO(
        int joinId,
        int meetingId,
        String title,
        String description,
        String location,
        Timestamp startTime,
        Timestamp endTime,
        String status
) {
    public MeetingCardDTO(int joinId, int meetingId, String title, String description, String location,
                          Timestamp startTime, Timestamp endTime, String status) {
        this.joinId = joinId;
        this.meetingId = meetingId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }
}
