package org.vdt.qlch.meetinghistoryservice.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.vdt.commonlib.utils.Constants;
import org.vdt.qlch.meetinghistoryservice.model.MeetingHistory;

@Component
@RequiredArgsConstructor
public class HistoryWSProducer {

    private final KafkaTemplate<String, MeetingHistory> kafkaTemplate;

    public void send(MeetingHistory meetingHistory) {
        kafkaTemplate.send(String.format(Constants.MEETING_WS_TOPIC_FORMAT, meetingHistory.getMeetingId()),
                meetingHistory);
    }

}
