package org.vdt.qlch.meetingservice.dto.redis;

import lombok.Builder;
import org.vdt.qlch.meetingservice.dto.response.JoinDTO;
import org.vdt.qlch.meetingservice.dto.response.UserDTO;
import org.vdt.qlch.meetingservice.model.MeetingJoin;

@Builder
public record OnlineUserDTO(
        String id,
        String fullName,
        String picture,
        JoinDTO join
) {

    public static OnlineUserDTO from(MeetingJoin join, UserDTO user) {
        return OnlineUserDTO.builder()
                .id(user.id())
                .fullName(user.lastName() + " " + user.firstName())
                .picture(user.picture())
                .join(JoinDTO.from(join))
                .build();
    }

}
