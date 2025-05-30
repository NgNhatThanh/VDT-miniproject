package org.vdt.qlch.meetingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdt.commonlib.exception.BadRequestException;
import org.vdt.commonlib.utils.AuthenticationUtil;
import org.vdt.qlch.meetingservice.dto.request.CreateMeetingDTO;
import org.vdt.qlch.meetingservice.dto.request.JoinDTO;
import org.vdt.qlch.meetingservice.dto.response.MeetingDTO;
import org.vdt.qlch.meetingservice.model.Meeting;
import org.vdt.qlch.meetingservice.model.MeetingLocation;
import org.vdt.qlch.meetingservice.repository.MeetingLocationRepository;
import org.vdt.qlch.meetingservice.repository.MeetingRepository;
import org.vdt.qlch.meetingservice.utils.Constants;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;

    private final MeetingLocationRepository locationRepository;

    private final UserService userService;

    @Transactional
    public MeetingDTO createMeeting(CreateMeetingDTO dto) {
        if(dto.startTime().isAfter(dto.endTime())){
            throw new BadRequestException(Constants.ErrorCode.STARTTIME_AFTER_ENDTIME_ERROR);
        }
        if(dto.startTime().isBefore(LocalDateTime.now().plusMinutes(15))){
            throw new BadRequestException(Constants.ErrorCode.STARTTIME_BEFORE_NOW_ERROR);
        }
        MeetingLocation location = locationRepository.findById(dto.locationId())
                .orElseThrow(() -> new BadRequestException(Constants.ErrorCode.MEETING_LOCATION_NOT_FOUND));
        boolean usersExists = userService.checkExistById(dto.joins().stream().map(JoinDTO::userId).toList());
        if(!usersExists){
            throw new BadRequestException(Constants.ErrorCode.PARTICIPANT_NOT_FOUND);
        }
        String userId = AuthenticationUtil.extractUserId();
        Meeting meeting = Meeting.builder()
                .title(dto.title())
                .description(dto.description())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .location(location)
                .createdBy(userId)
                .build();
        return MeetingDTO.from(meetingRepository.save(meeting));
    }

}
