package org.vdt.qlch.meetinghistoryservice.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.vdt.commonlib.dto.MeetingHistoryMessage;
import org.vdt.commonlib.utils.Constants;
import org.vdt.qlch.meetinghistoryservice.model.MeetingHistory;
import org.vdt.qlch.meetinghistoryservice.producer.HistoryWSProducer;
import org.vdt.qlch.meetinghistoryservice.service.MeetingHistoryService;
import org.vdt.qlch.meetinghistoryservice.utils.KafkaUtils;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private final MeetingHistoryService meetingHistoryService;

    private final HistoryWSProducer producer;

    private final KafkaUtils kafkaUtils;

    @Bean
    public AdminClient adminClient(){
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return AdminClient.create(props);
    }

    @KafkaListener(topics = Constants.NEW_MEETING_HISTORY_TOPIC,
            groupId = Constants.NEW_MEETING_HISTORY_GROUP)
    private void newEventListener(MeetingHistoryMessage message) {
        String topic = String.format(Constants.MEETING_WS_TOPIC_FORMAT, message.meetingId());
        MeetingHistory history = meetingHistoryService.save(message);
        if(!kafkaUtils.topicExists(topic)){
            kafkaUtils.createTopic(topic);
        }
        producer.send(history);
    }

}
