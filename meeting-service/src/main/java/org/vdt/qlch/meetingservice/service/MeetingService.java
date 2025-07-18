package org.vdt.qlch.meetingservice.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdt.commonlib.dto.MeetingHistoryMessage;
import org.vdt.commonlib.dto.RecordExistDTO;
import org.vdt.commonlib.dto.UserDTO;
import org.vdt.commonlib.exception.BadRequestException;
import org.vdt.commonlib.model.MeetingHistoryType;
import org.vdt.commonlib.utils.AuthenticationUtil;
import org.vdt.commonlib.utils.JwtUtil;
import org.vdt.commonlib.utils.ScheduleUtil;
import org.vdt.qlch.meetingservice.dto.redis.OnlineUserDTO;
import org.vdt.qlch.meetingservice.dto.request.*;
import org.vdt.qlch.meetingservice.dto.request.JoinDTO;
import org.vdt.qlch.meetingservice.dto.response.*;
import org.vdt.qlch.meetingservice.model.*;
import org.vdt.qlch.meetingservice.model.enums.DocumentStatus;
import org.vdt.qlch.meetingservice.model.enums.MeetingJoinStatus;
import org.vdt.qlch.meetingservice.producer.MeetingHistoryProducer;
import org.vdt.qlch.meetingservice.repository.*;
import org.vdt.qlch.meetingservice.service.redis.MeetingRedisService;
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

    private final PrivateNoteRepository privateNoteRepository;

    private final UserService userService;

    private final DocumentService documentService;

    private final JwtUtil jwtUtil;

    private final MeetingRedisService meetingRedisService;

    private final MeetingHistoryProducer historyProducer;

    private final ScheduleUtil scheduleUtil;

    @Transactional
    public MeetingDTO createMeeting(@Valid CreateMeetingDTO dto) {
        if(dto.startTime().isAfter(dto.endTime())){
            throw new BadRequestException(Constants.ErrorCode.STARTTIME_AFTER_ENDTIME_ERROR);
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

        if(dto.documentIds() != null) {
            List<Integer> documentIds = new ArrayList<>(new HashSet<>(dto.documentIds()));
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
        scheduleUtil.scheduleTask(meeting.getEndTime(), () ->
                historyProducer.send(MeetingHistoryMessage.builder()
                                .meetingId(meeting.getId())
                                .type(MeetingHistoryType.MEETING_ENDED)
                                .content("Cuộc họp đã kết thúc")
                        .build()));
        return MeetingDTO.from(meeting);
    }

    public List<MeetingCardDTO> getForUserCalendar(LocalDate startDate,
                                               LocalDate endDate) {
        if(startDate.isAfter(endDate)){
            throw new BadRequestException(Constants.ErrorCode.STARTDATE_AFTER_ENDDATE_ERROR);
        }
        String userId = AuthenticationUtil.extractUserId();
        return meetingJoinRepository.findAllByUserWithinDate(userId, startDate, endDate);
    }

    public MeetingDetailDTO getDetail(int meetingId) {
        String userId = AuthenticationUtil.extractUserId();
        MeetingJoin join = meetingJoinRepository.findByUserIdAndMeetingId(userId, meetingId);
        if(join == null){
            throw new BadRequestException(Constants.ErrorCode.MEETING_NOT_FOUND);
        }
        return MeetingDetailDTO.from(join);
    }

    @Transactional
    public MeetingDetailDTO updateJoin(@Valid JoinUpdateDTO dto) {
        MeetingJoin join = meetingJoinRepository.findById(dto.joinId())
                .orElseThrow(() -> new BadRequestException(Constants.ErrorCode.MEETING_NOT_FOUND));
        String userId = AuthenticationUtil.extractUserId();
        if(!join.getUserId().equals(userId)){
            throw new BadRequestException(Constants.ErrorCode.PARTICIPANT_NOT_FOUND);
        }
        MeetingJoinStatus status = MeetingJoinStatus.fromString(dto.status().toUpperCase());
        if(status == MeetingJoinStatus.REJECTED){
            if(dto.reason() == null || dto.reason().isEmpty())
                throw new BadRequestException(Constants.ErrorCode.REJECT_WITH_NO_REASON_ERROR);
            else
                join.setRejectReason(dto.reason());
        }
        join.setStatus(status);
        join.setUpdatedBy(userId);
        join = meetingJoinRepository.save(join);
        updateParticipantsCache(join.getMeeting().getId(), join);
        return MeetingDetailDTO.from(join);
    }

    public org.vdt.qlch.meetingservice.dto.response.JoinDTO joinMeeting(int meetingId) {
        String userId = AuthenticationUtil.extractUserId();
        MeetingJoin join = meetingJoinRepository.findByUserIdAndMeetingId(userId, meetingId);
        if(join == null){
            throw new BadRequestException(Constants.ErrorCode.PARTICIPANT_NOT_FOUND);
        }
        if(join.getStatus() != MeetingJoinStatus.ACCEPTED){
            throw new BadRequestException(Constants.ErrorCode.MEETING_JOIN_ERROR);
        }
        if(join.getMeeting().getEndTime().isBefore(LocalDateTime.now())){
            throw new BadRequestException(Constants.ErrorCode.MEETING_ENDED_ERROR);
        }
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        UserDTO user = jwtUtil.extractUser(jwt);
        historyProducer.send(MeetingHistoryMessage.builder()
                        .meetingId(meetingId)
                        .content(user.fullName() + " đã tham gia cuộc họp")
                        .type(MeetingHistoryType.USER_JOINED)
                        .build());
        meetingRedisService.addUserOnline(OnlineUserDTO.from(join, user), meetingId);
        return org.vdt.qlch.meetingservice.dto.response.JoinDTO.from(join);
    }

    public MeetingHeaderInfoDTO getHeaderInfo(int meetingId) {
        String userId = AuthenticationUtil.extractUserId();
        MeetingJoin join = meetingJoinRepository.findByUserIdAndMeetingId(userId, meetingId);
        if(join == null){
            throw new BadRequestException(Constants.ErrorCode.PARTICIPANT_NOT_FOUND);
        }
        if(join.getStatus() != MeetingJoinStatus.ACCEPTED){
            throw new BadRequestException(Constants.ErrorCode.MEETING_JOIN_ERROR);
        }
        if(join.getMeeting().getEndTime().isBefore(LocalDateTime.now())){
            throw new BadRequestException(Constants.ErrorCode.MEETING_ENDED_ERROR);
        }
        MeetingDTO meetingInfo = MeetingDTO.from(join.getMeeting());
        Set<OnlineUserDTO> onlineUsers = meetingRedisService.getUserOnlineList(meetingId);
        return new MeetingHeaderInfoDTO(meetingInfo, onlineUsers.stream().toList());
    }

    public void leaveMeeting(String userId, int meetingId) {
        OnlineUserDTO user = meetingRedisService.removeUserOnline(userId, meetingId);
        if(user == null){
            throw new BadRequestException(Constants.ErrorCode.PARTICIPANT_NOT_FOUND);
        }
        historyProducer.send(MeetingHistoryMessage.builder()
                .meetingId(meetingId)
                .content(user.fullName() + " đã rời cuộc họp")
                .type(MeetingHistoryType.USER_LEFT)
                .build());
    }

    public RecordExistDTO checkJoin(int meetingId) {
        String userId = AuthenticationUtil.extractUserId();
        MeetingJoin join = meetingJoinRepository.findByUserIdAndMeetingId(userId, meetingId);
        return new RecordExistDTO(join != null && join.getStatus() != MeetingJoinStatus.REJECTED);
    }

    public List<NoteDTO> getListNote(int meetingId) {
        String userId = AuthenticationUtil.extractUserId();
        List<MeetingPrivateNote> notes = privateNoteRepository.findAllByMeeting_IdAndCreatedBy(meetingId, userId);
        return notes.stream()
                .sorted(Comparator.comparing(MeetingPrivateNote::getUpdatedAt).reversed())
                .map(NoteDTO::from).toList();
    }

    public NoteDTO addNote(@Valid CreateNoteDTO dto) {
        Meeting meeting = meetingRepository.findById(dto.meetingId())
                .orElseThrow(() -> new BadRequestException(Constants.ErrorCode.MEETING_NOT_FOUND));
        String userId = AuthenticationUtil.extractUserId();
        MeetingPrivateNote note = MeetingPrivateNote.builder()
                .meeting(meeting)
                .content(dto.content())
                .createdBy(userId)
                .build();
        privateNoteRepository.save(note);
        return NoteDTO.from(note);
    }

    public NoteDTO updateNote(@Valid UpdateNoteDTO dto) {
        String userId = AuthenticationUtil.extractUserId();
        MeetingPrivateNote note = privateNoteRepository.findByIdAndCreatedBy(dto.noteId(), userId)
                .orElseThrow(() -> new BadRequestException(Constants.ErrorCode.NOTE_NOT_FOUND));
        note.setContent(dto.content());
        privateNoteRepository.save(note);
        return NoteDTO.from(note);
    }

    public void deleteNote(int noteId) {
        String userId = AuthenticationUtil.extractUserId();
        MeetingPrivateNote note = privateNoteRepository.findByIdAndCreatedBy(noteId, userId)
                .orElseThrow(() -> new BadRequestException(Constants.ErrorCode.NOTE_NOT_FOUND));
        privateNoteRepository.delete(note);
    }

    public List<ParticipantDTO> getListParticipants(int meetingId) {
        return meetingRedisService.getListParticipants(meetingId);
    }

    @CachePut(value = "meeting-participants", key = "#meetingId")
    public List<ParticipantDTO> updateParticipantsCache(int meetingId, MeetingJoin join) {
        List<ParticipantDTO> participants = meetingRedisService.getListParticipants(meetingId);
        return participants.stream()
                .map(p -> p.joinId() == join.getId() ?
                        ParticipantDTO.builder()
                                .joinId(join.getId())
                                .picture(p.picture())
                                .fullName(p.fullName())
                                .status(join.getStatus())
                                .roles(p.roles())
                                .build(): p)
                .toList();
    }

}
