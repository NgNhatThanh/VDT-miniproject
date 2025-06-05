package org.vdt.qlch.meetingservice.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaUtils {

    private final AdminClient adminClient;

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
