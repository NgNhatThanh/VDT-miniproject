package org.vdt.qlch.meetinghistoryservice;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.vdt.commonlib.config.CorsConfig;
import org.vdt.commonlib.dto.MeetingHistoryMessage;
import org.vdt.qlch.meetinghistoryservice.utils.KafkaUtils;

@SpringBootApplication(
        scanBasePackages = {"org.vdt.qlch.meetinghistoryservice", "org.vdt.commonlib"}
)
@EnableConfigurationProperties({CorsConfig.class})
@EnableKafka
public class MeetingHistoryServiceApplication implements CommandLineRunner {

//    @Autowired
//    private KafkaUtils kafkaUtils;

    public static void main(String[] args) {
        SpringApplication.run(MeetingHistoryServiceApplication.class, args);
    }

//    @KafkaListener(topicPattern = "meeting-mq-.*",
//            groupId = "consumerGroup-" + "#{T(java.util.UUID).randomUUID()}")
//    public void listen(MeetingHistoryMessage record) {
//        System.out.println(record.toString());
//    }

    @Override
    public void run(String... args) throws Exception {
//        kafkaUtils.unsubscribe("meeting-mq-25");
//        new Thread(() -> {
//            try {
//                Thread.sleep(30000);
//                kafkaUtils.unsubscribeAll();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
//
//        new Thread(() -> {
//            try {
//                Thread.sleep(60000);
//                kafkaUtils.subscribe("meeting-mq-25");
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();

    }
}
