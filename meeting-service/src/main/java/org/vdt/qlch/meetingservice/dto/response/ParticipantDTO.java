package org.vdt.qlch.meetingservice.dto.response;

import lombok.Builder;
import org.vdt.qlch.meetingservice.model.enums.MeetingJoinStatus;

import java.util.List;

@Builder
public record ParticipantDTO(
        int joinId,
        String fullName,
        String picture,
        MeetingJoinStatus status,
        List<RoleDTO> roles
) {
}
