package org.vdt.qlch.meetingservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vdt.qlch.meetingservice.dto.request.CreateMeetingDTO;
import org.vdt.qlch.meetingservice.dto.response.MeetingDTO;
import org.vdt.qlch.meetingservice.service.MeetingService;

@RestController
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping("/create")
    public ResponseEntity<MeetingDTO> createMeeting(@RequestBody CreateMeetingDTO dto){
        return ResponseEntity.ok(meetingService.createMeeting(dto));
    }

}
