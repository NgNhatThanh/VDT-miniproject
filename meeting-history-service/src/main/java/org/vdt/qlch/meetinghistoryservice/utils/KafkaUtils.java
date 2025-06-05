package org.vdt.qlch.meetinghistoryservice.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;
import org.vdt.commonlib.dto.MeetingHistoryMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaUtils {

    private final Map<String,
            ConcurrentMessageListenerContainer<String, MeetingHistoryMessage>> activeContainers = new ConcurrentHashMap<>();

    private final KafkaListenerEndpointRegistry registry;

    private final ConcurrentKafkaListenerContainerFactory<String, MeetingHistoryMessage> kafkaListenerContainerFactory;

    private final ConsumerFactory<String, MeetingHistoryMessage> consumerFactory;

    private final MessageListener<String, MeetingHistoryMessage> myMessageListener = (data) -> {
        System.out.println("Dynamic Listener - Topic: " + data.topic() + ", Partition: " + data.partition() + ", Offset: " + data.offset() + ", Message: " + data.value());
    };

    public void subscribeTopics(String listenerId, String topic) {
        unsubscribe(listenerId);

        System.out.println("Attempting to subscribe listener '" + listenerId + "' to topic: " + topic);

        ContainerProperties containerProperties = new ContainerProperties(topic);
        containerProperties.setMessageListener(myMessageListener);
        containerProperties.setGroupId(listenerId);

        // Tạo một ConcurrentMessageListenerContainer mới
        ConcurrentMessageListenerContainer<String, MeetingHistoryMessage> container =
                new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);

        // Đăng ký container vào registry (không bắt buộc nhưng hữu ích để quản lý)
//         registry.registerListenerContainer(container, true); // ID trong registry là listenerId

        // Khởi tạo container (quan trọng để nó bắt đầu poll tin nhắn)
        container.setBeanName("dynamicListener-" + listenerId); // Đặt tên bean để dễ debug
//        container.;

        // Bắt đầu container
        container.start();
        activeContainers.put(listenerId, container);
        System.out.println("Listener '" + listenerId + "' subscribed and started.");
    }

    public void unsubscribe(String listenerId) {
        ConcurrentMessageListenerContainer<String, MeetingHistoryMessage> container =
                activeContainers.get(listenerId);
        if (container != null) {
            System.out.println("Attempting to unsubscribe listener '" + listenerId + "'");
            container.stop();
            activeContainers.remove(listenerId);
            System.out.println("Listener '" + listenerId + "' stopped and unsubscribed.");
        } else {
            System.out.println("Listener '" + listenerId + "' not found or already stopped.");
        }
    }

    public Set<String> getActiveListenerIds() {
        return activeContainers.keySet();
    }

}
