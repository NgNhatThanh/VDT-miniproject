package org.vdt.qlch.meetinghistoryservice.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;
import org.vdt.commonlib.dto.MeetingHistoryMessage;
import org.vdt.commonlib.utils.Constants;
import org.vdt.qlch.meetinghistoryservice.model.MeetingHistory;
import org.vdt.qlch.meetinghistoryservice.producer.HistoryWSProducer;
import org.vdt.qlch.meetinghistoryservice.service.MeetingHistoryService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaUtils {

    private final String uniqId = UUID.randomUUID().toString();

    private final AdminClient adminClient;

    private final Map<String,
            ConcurrentMessageListenerContainer<String, MeetingHistoryMessage>> activeContainers = new ConcurrentHashMap<>();

    private final KafkaListenerEndpointRegistry registry;

    private final ConsumerFactory<String, MeetingHistoryMessage> consumerFactory;

    private final MeetingHistoryService meetingHistoryService;

    private final HistoryWSProducer producer;

    @KafkaListener(topics = Constants.NEW_MEETING_HISTORY_TOPIC,
            groupId = Constants.NEW_MEETING_HISTORY_GROUP)
    private void newEventListener(MeetingHistoryMessage message) {
        String topic = String.format(Constants.MEETING_WS_TOPIC_FORMAT, message.meetingId());
        MeetingHistory history = meetingHistoryService.save(message);
        if(!topicExists(topic)){
            createTopic(topic);
        }
        producer.send(history);
    }

    private final MessageListener<String, MeetingHistoryMessage> myMessageListener = (data) -> {
        System.out.println("Dynamic Listener - Topic: " + data.topic() + ", Partition: " + data.partition() + ", Offset: " + data.offset() + ", Message: " + data.value());
    };

    public void subscribeTopics(String topic) {
        String groupId = topic + "-" + uniqId;
        ContainerProperties containerProperties = new ContainerProperties(topic);
        containerProperties.setMessageListener(myMessageListener);
        containerProperties.setGroupId(groupId);
        ConcurrentMessageListenerContainer<String, MeetingHistoryMessage> container =
                new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);
//         registry.registerListenerContainer(container, true); // ID trong registry là listenerId
        container.setBeanName(groupId); // Đặt tên bean để dễ debug
        container.start();
        activeContainers.put(groupId, container);
    }

    public void unsubscribe(String topic) {
        String groupId = topic + "-" + uniqId;
        ConcurrentMessageListenerContainer<String, MeetingHistoryMessage> container =
                activeContainers.get(groupId);
        if (container != null) {
            container.stop();
            activeContainers.remove(groupId);
        } else {
            System.out.println("Listener '" + groupId + "' not found or already stopped.");
        }
    }

    public boolean topicExists(String topic) {
        try{
            ListTopicsResult listTopicsResult = adminClient.listTopics();
            return listTopicsResult.names().get()
                    .stream().anyMatch(n -> n.equals(topic));
        }
        catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    public void createTopic(String topic){
        NewTopic newTopic = new NewTopic(topic, 3, (short) 1);
        adminClient.createTopics(List.of(newTopic));
        log.info("Topic created: {}",topic);
    }

}
