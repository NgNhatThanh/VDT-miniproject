package org.vdt.qlch.meetingservice.dto.response;

import lombok.Builder;
import org.vdt.qlch.meetingservice.model.MeetingRole;

@Builder
public record RoleDTO(
        int id,
        String name,
        String description
) {

    public static RoleDTO from(MeetingRole meetingRole) {
    return RoleDTO.builder()
            .id(meetingRole.getId())
            .name(meetingRole.getName())
            .description(meetingRole.getDescription())
            .build();
    }

}
