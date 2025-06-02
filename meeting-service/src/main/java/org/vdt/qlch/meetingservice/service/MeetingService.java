package org.vdt.qlch.meetingservice.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdt.commonlib.exception.BadRequestException;
import org.vdt.commonlib.utils.AuthenticationUtil;
import org.vdt.qlch.meetingservice.dto.request.CreateMeetingDTO;
import org.vdt.qlch.meetingservice.dto.request.JoinDTO;
import org.vdt.qlch.meetingservice.dto.response.MeetingCardDTO;
import org.vdt.qlch.meetingservice.dto.response.MeetingDTO;
import org.vdt.qlch.meetingservice.dto.response.MeetingDetailDTO;
import org.vdt.qlch.meetingservice.model.*;
import org.vdt.qlch.meetingservice.model.enums.DocumentStatus;
import org.vdt.qlch.meetingservice.model.enums.MeetingJoinStatus;
import org.vdt.qlch.meetingservice.repository.*;
import org.vdt.qlch.meetingservice.utils.Constants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;

    private final MeetingLocationRepository locationRepository;

    private final MeetingDocumentRepository meetingDocumentRepository;

    private final MeetingJoinRepository meetingJoinRepository;

    private final MeetingRoleRepository meetingRoleRepository;

    private final UserService userService;

    private final DocumentService documentService;

    @Transactional
    public MeetingDTO createMeeting(@Valid CreateMeetingDTO dto) {
        if(dto.startTime().isAfter(dto.endTime())){
            throw new BadRequestException(Constants.ErrorCode.STARTTIME_AFTER_ENDTIME_ERROR);
        }
        if(dto.startTime().isBefore(LocalDateTime.now().plusMinutes(15))){
            throw new BadRequestException(Constants.ErrorCode.STARTTIME_BEFORE_NOW_ERROR);
        }

        MeetingLocation location = locationRepository.findById(dto.locationId())
                .orElseThrow(() -> new BadRequestException(Constants.ErrorCode.MEETING_LOCATION_NOT_FOUND));
        String userId = AuthenticationUtil.extractUserId();
        Meeting meeting = Meeting.builder()
                .title(dto.title())
                .description(dto.description())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .location(location)
                .createdBy(userId)
                .build();
        meetingRepository.save(meeting);

        List<String> userIds = new ArrayList<>(
                new HashSet<>(dto.joins()
                        .stream().map(JoinDTO::userId).toList())
        );
        boolean usersExists = userService.checkExistById(userIds);
        if(!usersExists){
            throw new BadRequestException(Constants.ErrorCode.PARTICIPANT_NOT_FOUND);
        }
        List<MeetingRole> allRoles = meetingRoleRepository.findAll();
        Map<Integer, MeetingRole> roleMap = new HashMap<>();
        allRoles.forEach(r -> roleMap.put(r.getId(), r));
        Map<String, HashSet<Integer>> joinsMap = new HashMap<>();
        dto.joins().forEach(j -> {
            HashSet<Integer> rs = joinsMap.getOrDefault(j.userId(), new HashSet<>());
            rs.add(j.roleId());
            joinsMap.put(j.userId(), rs);
        });
        List<MeetingJoin> joins = new ArrayList<>();
        joinsMap.forEach((uId, roleIds) -> {
            List<MeetingRole> roles = new ArrayList<>();
            for(Integer roleId : roleIds) {
                MeetingRole role = roleMap.getOrDefault(roleId, null);
                if(role == null){
                    throw new BadRequestException(Constants.ErrorCode.MEETING_ROLE_NOT_FOUND);
                }
                roles.add(role);
            }
            joins.add(MeetingJoin.builder()
                            .userId(uId)
                            .createdBy(userId)
                            .status(userId.equals(uId) ? MeetingJoinStatus.ACCEPTED : MeetingJoinStatus.PENDING)
                            .meeting(meeting)
                            .roles(roles)
                            .build());
        });
        meetingJoinRepository.saveAll(joins);

        if(dto.documentsIds() != null) {
            List<Integer> documentIds = new ArrayList<>(new HashSet<>(dto.documentsIds()));
            boolean documentsExists = documentService.checkExistById(documentIds);
            if (!documentsExists) {
                throw new BadRequestException(Constants.ErrorCode.DOCUMENT_NOT_FOUND);
            }
            List<MeetingDocument> documents = documentIds.stream()
                    .map(id -> (MeetingDocument)MeetingDocument.builder()
                            .meeting(meeting)
                            .documentId(id)
                            .status(DocumentStatus.APPROVED)
                            .approvedBy(userId)
                            .createdBy(userId)
                            .build())
                    .toList();
            meetingDocumentRepository.saveAll(documents);
        }
        return MeetingDTO.from(meetingRepository.save(meeting));
    }

    public List<MeetingCardDTO> getForUserCalendar(String userId,
                                               LocalDate startDate,
                                               LocalDate endDate) {
        if(startDate.isAfter(endDate)){
            throw new BadRequestException(Constants.ErrorCode.STARTDATE_AFTER_ENDDATE_ERROR);
        }
        boolean userExists = userService.checkExistById(Collections.singletonList(userId));
        if(!userExists){
            throw new BadRequestException(Constants.ErrorCode.PARTICIPANT_NOT_FOUND);
        }
        List<MeetingCardDTO> meetings = meetingJoinRepository.findAllByUserWithinDate(userId, startDate, endDate);
        return meetings;
    }

    public MeetingDetailDTO getDetail(int meetingId) {
        String userId = AuthenticationUtil.extractUserId();
        MeetingJoin join = meetingJoinRepository.findByUserIdAndMeetingId(userId, meetingId);
        if(join == null){
            throw new BadRequestException(Constants.ErrorCode.MEETING_NOT_FOUND);
        }
        MeetingDetailDTO res = MeetingDetailDTO.from(join);
        return res;
    }
}
