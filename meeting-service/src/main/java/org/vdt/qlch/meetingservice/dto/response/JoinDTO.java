package org.vdt.qlch.meetingservice.dto.response;

import lombok.Builder;
import org.vdt.qlch.meetingservice.model.MeetingJoin;
import org.vdt.qlch.meetingservice.model.enums.MeetingJoinStatus;

import java.util.List;

@Builder
public record JoinDTO(
        int id,
        MeetingJoinStatus status,
        List<RoleDTO> roles
) {

    public static JoinDTO from(MeetingJoin join){
        return JoinDTO.builder()
                .id(join.getId())
                .status(join.getStatus())
                .roles(join.getRoles().stream().map(RoleDTO::from).toList())
                .build();
    }

}
