package org.vdt.qlch.meetinghistoryservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.vdt.commonlib.dto.MeetingHistoryMessage;
import org.vdt.commonlib.exception.BadRequestException;
import org.vdt.qlch.meetinghistoryservice.dto.MeetingHistoryDTO;
import org.vdt.qlch.meetinghistoryservice.model.MeetingHistory;
import org.vdt.qlch.meetinghistoryservice.repository.MeetingHistoryRepository;
import org.vdt.qlch.meetinghistoryservice.utils.Constants;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingHistoryService {

    private final MeetingHistoryRepository meetingHistoryRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private final MeetingService meetingService;

    public MeetingHistory save(MeetingHistoryMessage message) {
        MeetingHistory history = MeetingHistory.builder()
                .meetingId(message.meetingId())
                .content(message.content())
                .type(message.type())
                .createdBy("")
                .build();
        return meetingHistoryRepository.save(history);
    }

    public void sendMessage(MeetingHistory message) {
        messagingTemplate.convertAndSendToUser(String.valueOf(message.getMeetingId()), "/history", message);
    }

    public List<MeetingHistoryDTO> get(int meetingId, int limit, int offset) {
        boolean legit = meetingService.checkJoin(meetingId);
        if(!legit){
            throw new BadRequestException(Constants.ErrorCode.MEETING_JOIN_ERROR);
        }
        List<MeetingHistory> histories = meetingHistoryRepository.getList(meetingId, limit, offset);
        return histories.stream()
                .map(MeetingHistoryDTO::from)
                .toList();
    }
}
