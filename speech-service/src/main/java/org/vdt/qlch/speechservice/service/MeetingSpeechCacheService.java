package org.vdt.qlch.speechservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.vdt.commonlib.dto.UserDTO;
import org.vdt.qlch.speechservice.dto.response.SpeechDTO;
import org.vdt.qlch.speechservice.model.MeetingSpeech;
import org.vdt.qlch.speechservice.repository.MeetingSpeechRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingSpeechCacheService {

    private final MeetingSpeechRepository speechRepository;

    private final UserService userService;

    @Cacheable(value = "meeting-speeches", key = "#meetingId")
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

}
