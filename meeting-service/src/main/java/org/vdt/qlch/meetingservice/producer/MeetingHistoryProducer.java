package org.vdt.qlch.meetingservice.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.vdt.commonlib.dto.MeetingHistoryMessage;
import org.vdt.commonlib.utils.Constants;

@Component
@RequiredArgsConstructor
public class MeetingHistoryProducer {

    private final KafkaTemplate<String, MeetingHistoryMessage> kafkaTemplate;

    public void send(MeetingHistoryMessage message) {
        kafkaTemplate.send(Constants.NEW_MEETING_HISTORY_TOPIC, message);
    }

}
