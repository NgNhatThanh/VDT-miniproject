package org.vdt.qlch.meetingservice.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.vdt.commonlib.dto.MeetingLeaveMessage;
import org.vdt.commonlib.utils.Constants;
import org.vdt.qlch.meetingservice.service.MeetingService;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final MeetingService meetingService;

    @KafkaListener(topics = Constants.MEETING_LEAVE_TOPIC,
            groupId = Constants.MEETING_LEAVE_GROUP)
    public void listenUserLeave(MeetingLeaveMessage message){
        meetingService.leaveMeeting(message.userId(), message.meetingId());
    }

}
