package org.vdt.qlch.meetingservice.dto.response;

import lombok.Builder;
import org.vdt.qlch.meetingservice.dto.redis.OnlineUserDTO;

import java.util.List;

@Builder
public record MeetingHeaderInfoDTO(
        MeetingDTO info,
        List<OnlineUserDTO> onlineUsers
) {
}
