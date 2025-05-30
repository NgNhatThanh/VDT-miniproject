package org.vdt.qlch.meetingservice.dto.response;

import lombok.Builder;
import org.vdt.qlch.meetingservice.model.Meeting;

import java.time.LocalDateTime;

@Builder
public record MeetingDTO(
        String title,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocationDTO location
        ) {

        public static MeetingDTO from(Meeting meeting) {
                return MeetingDTO.builder()
                        .title(meeting.getTitle())
                        .description(meeting.getDescription())
                        .startTime(meeting.getStartTime())
                        .endTime(meeting.getEndTime())
                        .location(LocationDTO.from(meeting.getLocation()))
                        .build();
        }

}
