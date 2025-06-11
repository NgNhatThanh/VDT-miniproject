package org.vdt.qlch.speechservice.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdt.commonlib.dto.MeetingHistoryMessage;
import org.vdt.commonlib.dto.UserDTO;
import org.vdt.commonlib.exception.BadRequestException;
import org.vdt.commonlib.model.MeetingHistoryType;
import org.vdt.commonlib.utils.AuthenticationUtil;
import org.vdt.commonlib.utils.JwtUtil;
import org.vdt.qlch.speechservice.dto.request.SpeechRegisterDTO;
import org.vdt.qlch.speechservice.dto.request.UpdateSpeechDTO;
import org.vdt.qlch.speechservice.dto.response.SpeechDTO;
import org.vdt.qlch.speechservice.model.MeetingSpeech;
import org.vdt.qlch.speechservice.model.SpeechStatus;
import org.vdt.qlch.speechservice.producer.MeetingHistoryProducer;
import org.vdt.qlch.speechservice.repository.MeetingSpeechRepository;
import org.vdt.qlch.speechservice.utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingSpeechService {

    private final MeetingSpeechRepository speechRepository;

    private final MeetingHistoryProducer historyProducer;

    private final UserService userService;

    private final JwtUtil jwtUtil;

    @Transactional
    public void register(@Valid SpeechRegisterDTO dto) {
        String userId = AuthenticationUtil.extractUserId();
        MeetingSpeech meetingSpeech = MeetingSpeech.builder()
                .meetingId(dto.meetingId())
                .content(dto.content())
                .duration(dto.duration())
                .status(SpeechStatus.PENDING)
                .speakerId(userId)
                .createdBy(userId)
                .build();
        speechRepository.save(meetingSpeech);
        String jwt = AuthenticationUtil.extractJwt();
        UserDTO user = jwtUtil.extractUser(jwt);
        historyProducer.send(MeetingHistoryMessage.builder()
                        .meetingId(meetingSpeech.getMeetingId())
                        .type(MeetingHistoryType.SPEECH_REGISTERED)
                        .content(String.format("%s đã đăng ký phát biểu", user.fullName()))
                .build());
    }


    public List<SpeechDTO> getList(int meetingId) {
        List<MeetingSpeech> meetingSpeeches = speechRepository.getAllByMeetingIdOrderByCreatedAt(meetingId);
        Set<String> speakerIds = meetingSpeeches.stream()
                .map(MeetingSpeech::getSpeakerId)
                .collect(Collectors.toSet());
        List<UserDTO> speakers = userService.getListByIds(speakerIds.stream().toList());
        Map<String, UserDTO> speakerMap = new HashMap<>();
        speakers.forEach(speaker -> speakerMap.put(speaker.id(), speaker));
        return meetingSpeeches.stream()
                .map(sp -> SpeechDTO.builder()
                        .id(sp.getId())
                        .content(sp.getContent())
                        .duration(sp.getDuration())
                        .status(sp.getStatus())
                        .createdAt(sp.getCreatedAt())
                        .speakerFullName(speakerMap.get(sp.getSpeakerId()).fullName())
                        .build())
                .toList();
    }

    public void updateSpeech(@Valid UpdateSpeechDTO dto) {
        MeetingSpeech speech = speechRepository.findById(dto.speechId())
                .orElseThrow(() -> new BadRequestException(Constants.ErrorCode.SPEECH_NOT_FOUND));
        // should check the pre-status, but I'm lazy
        SpeechStatus status;
        try{
            status = SpeechStatus.fromString(dto.status());
        }
        catch (IllegalArgumentException e){
            throw new BadRequestException(org.vdt.commonlib.utils.Constants.ErrorCode.INVALID_ENUM_VALUE);
        }
        speech.setStatus(status);
        String userId = AuthenticationUtil.extractUserId();
        speech.setUpdatedBy(userId);
        speechRepository.save(speech);
        UserDTO user = userService.getById(speech.getSpeakerId());
        MeetingHistoryType type = null;
        String format = switch (status) {
            case APPROVED -> {
                type = MeetingHistoryType.SPEECH_APPROVED;
                yield "Đăng ký phát biểu của %s đã được duyệt";
            }
            case REJECTED -> {
                type = MeetingHistoryType.SPEECH_REJECTED;
                yield "Đăng ký phát biểu của %s bị từ chối";
            }
            case ON_GOING -> {
                type = MeetingHistoryType.SPEECH_STARTED;
                yield "%s bắt đầu phát biểu";
            }
            case ENDED -> {
                type = MeetingHistoryType.SPEECH_ENDED;
                yield "%s kết thúc phát biểu";
            }
            default -> "";
        };
        historyProducer.send(MeetingHistoryMessage.builder()
                        .meetingId(speech.getMeetingId())
                        .content(String.format(format, user.fullName()))
                        .type(type)
                .build());
    }
}
