package org.vdt.qlch.meetinghistoryservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.vdt.qlch.meetinghistoryservice.service.MeetingHistoryService;

@RestController
@RequiredArgsConstructor
public class MeetingHistoryController {

    private final MeetingHistoryService meetingHistoryService;

}
