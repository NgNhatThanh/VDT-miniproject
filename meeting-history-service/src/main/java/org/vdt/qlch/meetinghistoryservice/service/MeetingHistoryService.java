package org.vdt.qlch.meetinghistoryservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vdt.commonlib.dto.MeetingHistoryMessage;
import org.vdt.qlch.meetinghistoryservice.model.MeetingHistory;
import org.vdt.qlch.meetinghistoryservice.repository.MeetingHistoryRepository;

@Service
@RequiredArgsConstructor
public class MeetingHistoryService {

    private final MeetingHistoryRepository meetingHistoryRepository;

    public MeetingHistory save(MeetingHistoryMessage message) {
        MeetingHistory history = MeetingHistory.builder()
                .meetingId(message.meetingId())
                .content(message.content())
                .type(message.type())
                .build();
        return meetingHistoryRepository.save(history);
    }
}
