package org.vdt.qlch.meetingservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vdt.qlch.meetingservice.dto.request.CreateMeetingDTO;
import org.vdt.qlch.meetingservice.dto.response.MeetingCardDTO;
import org.vdt.qlch.meetingservice.dto.response.MeetingDTO;
import org.vdt.qlch.meetingservice.dto.response.MeetingDetailDTO;
import org.vdt.qlch.meetingservice.service.MeetingService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping("/create")
    public ResponseEntity<MeetingDTO> createMeeting(@RequestBody @Valid CreateMeetingDTO dto){
        return ResponseEntity.ok(meetingService.createMeeting(dto));
    }

    @GetMapping("/get-for-user-calendar")
    public ResponseEntity<List<MeetingCardDTO>> getMeetingForUser(@RequestParam String userId,
                                                                  @RequestParam LocalDate startDate,
                                                                  @RequestParam LocalDate endDate){
        return ResponseEntity.ok(meetingService.getForUserCalendar(userId, startDate, endDate));
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity<MeetingDetailDTO> getMeetingDetail(@PathVariable int meetingId){
        return ResponseEntity.ok(meetingService.getDetail(meetingId));
    }

}
