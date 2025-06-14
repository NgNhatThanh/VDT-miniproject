package org.vdt.qlch.speechservice.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingSpeechService {

    private final MeetingSpeechRepository speechRepository;

    private final MeetingHistoryProducer historyProducer;

    private final UserService userService;

    private final MeetingSpeechCacheService cacheService;

    private final JwtUtil jwtUtil;

    @Transactional
    @CachePut(value = "meeting-speeches", key = "#dto.meetingId()")
    public List<SpeechDTO> register(@Valid SpeechRegisterDTO dto) {
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
        List<SpeechDTO> cur = cacheService.getList(dto.meetingId());
        List<SpeechDTO> newList = new ArrayList<>(cur);
        if(cur.stream().noneMatch(s -> s.id() == meetingSpeech.getId())){
            newList.add(SpeechDTO.builder()
                    .id(meetingSpeech.getId())
                    .content(meetingSpeech.getContent())
                    .duration(meetingSpeech.getDuration())
                    .status(meetingSpeech.getStatus())
                    .createdAt(meetingSpeech.getCreatedAt())
                    .speakerFullName(user.fullName())
                    .build());
        }
        historyProducer.send(MeetingHistoryMessage.builder()
                        .meetingId(meetingSpeech.getMeetingId())
                        .type(MeetingHistoryType.SPEECH_REGISTERED)
                        .content(String.format("%s đã đăng ký phát biểu", user.fullName()))
                .build());
        return newList;
    }


    public List<SpeechDTO> getList(int meetingId) {
        return cacheService.getList(meetingId);
    }

    @CachePut(value = "meeting-speeches", key = "#dto.meetingId()")
    public List<SpeechDTO> updateSpeech(@Valid UpdateSpeechDTO dto) {
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

        List<SpeechDTO> cur = cacheService.getList(dto.meetingId());
        List<SpeechDTO> newList = cur.stream()
                .map(sp -> {
                    if (sp.id() == speech.getId()) {
                        return SpeechDTO.builder()
                                .id(speech.getId())
                                .content(speech.getContent())
                                .duration(speech.getDuration())
                                .status(speech.getStatus())
                                .createdAt(speech.getCreatedAt())
                                .speakerFullName(user.fullName())
                                .build();
                    }
                    return sp;
                })
                .toList();

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
        return newList;
    }
}
