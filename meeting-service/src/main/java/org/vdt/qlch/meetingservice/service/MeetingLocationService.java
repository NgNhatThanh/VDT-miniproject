package org.vdt.qlch.meetingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdt.commonlib.exception.BadRequestException;
import org.vdt.commonlib.utils.AuthenticationUtil;
import org.vdt.qlch.meetingservice.dto.response.LocationDTO;
import org.vdt.qlch.meetingservice.model.MeetingLocation;
import org.vdt.qlch.meetingservice.repository.MeetingLocationRepository;
import org.vdt.qlch.meetingservice.utils.Constants;

@Service
@RequiredArgsConstructor
public class MeetingLocationService {

    private final MeetingLocationRepository meetingLocationRepository;

    @Transactional
    public LocationDTO addLocation(LocationDTO dto) {
        if(meetingLocationRepository.existsByNameIgnoreCase(dto.name()))
            throw new BadRequestException(Constants.ErrorCode.MEETING_LOCATION_EXISTED);
        MeetingLocation meetingLocation = MeetingLocation.builder()
                .name(dto.name())
                .description(dto.description())
                .createdBy(AuthenticationUtil.extractUserId())
                .build();
        meetingLocationRepository.save(meetingLocation);
        return LocationDTO.from(meetingLocation);
    }

}
