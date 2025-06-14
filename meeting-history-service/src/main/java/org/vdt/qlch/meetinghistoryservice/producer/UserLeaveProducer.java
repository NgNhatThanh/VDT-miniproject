package org.vdt.qlch.meetinghistoryservice.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.vdt.commonlib.dto.MeetingLeaveMessage;
import org.vdt.commonlib.utils.Constants;

@Component
@RequiredArgsConstructor
public class UserLeaveProducer {

    private final KafkaTemplate<String, MeetingLeaveMessage> kafkaTemplate;

    public void send(MeetingLeaveMessage message) {
        kafkaTemplate.send(Constants.MEETING_LEAVE_TOPIC, message);
    }

}
