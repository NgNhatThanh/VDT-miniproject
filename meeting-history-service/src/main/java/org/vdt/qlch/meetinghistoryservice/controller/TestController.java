package org.vdt.qlch.meetinghistoryservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vdt.commonlib.model.MeetingHistoryType;
import org.vdt.qlch.meetinghistoryservice.model.MeetingHistory;
import org.vdt.qlch.meetinghistoryservice.service.MeetingHistoryService;
import org.vdt.qlch.meetinghistoryservice.utils.KafkaUtils;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final KafkaUtils kafkaUtils;
    
    private final MeetingHistoryService meetingHistoryService;

    @GetMapping("/send")
    public String send(@RequestParam int id){
        meetingHistoryService.sendMessage(MeetingHistory.builder()
                .type(MeetingHistoryType.USER_JOINED)
                .content("Content: " + id)
                .meetingId(26)
                .createdBy("")
                .build());
        return "ok";
    }

    @GetMapping("/unsub")
    public String unsub(@RequestParam String topic){
        kafkaUtils.unsubscribe(topic);
        return "unsubscribe successfully";
    }

}
