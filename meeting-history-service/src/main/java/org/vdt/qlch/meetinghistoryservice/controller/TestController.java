package org.vdt.qlch.meetinghistoryservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vdt.qlch.meetinghistoryservice.utils.KafkaUtils;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final KafkaUtils kafkaUtils;

    @GetMapping("/sub")
    public String sub(@RequestParam String groupId,
                      @RequestParam String topic) {
        kafkaUtils.subscribeTopics(groupId, topic);
        return "subscribe successfully";
    }

    @GetMapping("/unsub")
    public String unsub(@RequestParam String topic){
        kafkaUtils.unsubscribe(topic);
        return "unsubscribe successfully";
    }

}
