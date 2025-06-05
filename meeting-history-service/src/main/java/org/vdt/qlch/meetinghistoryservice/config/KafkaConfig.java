package org.vdt.qlch.meetinghistoryservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.vdt.commonlib.dto.MeetingHistoryMessage;
import org.vdt.commonlib.utils.Constants;
import org.vdt.qlch.meetinghistoryservice.model.MeetingHistory;
import org.vdt.qlch.meetinghistoryservice.producer.HistoryWSProducer;
import org.vdt.qlch.meetinghistoryservice.service.MeetingHistoryService;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final MeetingHistoryService meetingHistoryService;

    private final HistoryWSProducer producer;

    @KafkaListener(topics = Constants.NEW_MEETING_HISTORY_TOPIC,
            groupId = Constants.NEW_MEETING_HISTORY_GROUP)
    private void newEventListener(MeetingHistoryMessage message) {
        MeetingHistory history = meetingHistoryService.save(message);
        producer.send(history);
    }

}
