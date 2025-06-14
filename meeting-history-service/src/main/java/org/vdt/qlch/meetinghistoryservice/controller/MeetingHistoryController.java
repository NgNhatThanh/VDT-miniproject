package org.vdt.qlch.meetinghistoryservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vdt.qlch.meetinghistoryservice.dto.MeetingHistoryDTO;
import org.vdt.qlch.meetinghistoryservice.service.MeetingHistoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MeetingHistoryController {

    private final MeetingHistoryService meetingHistoryService;

    @GetMapping("/get-histories")
    public ResponseEntity<List<MeetingHistoryDTO>> getHistories(@RequestParam int meetingId,
                                                                @RequestParam int limit,
                                                                @RequestParam int offset) {
        return ResponseEntity.ok(meetingHistoryService.get(meetingId, limit, offset));
    }

}
