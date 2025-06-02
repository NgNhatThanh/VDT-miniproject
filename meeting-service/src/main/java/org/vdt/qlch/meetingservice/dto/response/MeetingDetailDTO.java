package org.vdt.qlch.meetingservice.dto.response;

import lombok.Builder;
import org.hibernate.validator.internal.util.privilegedactions.LoadClass;
import org.vdt.qlch.meetingservice.model.MeetingJoin;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MeetingDetailDTO(
        String title,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocationDTO location,
        JoinDTO join
) {

    public static MeetingDetailDTO from(MeetingJoin join) {
        return MeetingDetailDTO.builder()
                .title(join.getMeeting().getTitle())
                .description(join.getMeeting().getDescription())
                .startTime(join.getMeeting().getStartTime())
                .endTime(join.getMeeting().getEndTime())
                .location(LocationDTO.from(join.getMeeting().getLocation()))
                .join(JoinDTO.from(join))
                .build();
    }

}
