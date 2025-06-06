package org.vdt.qlch.meetingservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vdt.qlch.meetingservice.dto.request.CreateMeetingDTO;
import org.vdt.qlch.meetingservice.dto.request.JoinUpdateDTO;
import org.vdt.qlch.meetingservice.dto.response.*;
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
    public ResponseEntity<List<MeetingCardDTO>> getMeetingForUser(@RequestParam LocalDate startDate,
                                                                  @RequestParam LocalDate endDate){
        return ResponseEntity.ok(meetingService.getForUserCalendar(startDate, endDate));
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity<MeetingDetailDTO> getMeetingDetail(@PathVariable int meetingId){
        return ResponseEntity.ok(meetingService.getDetail(meetingId));
    }

    @PostMapping("/join-update")
    public ResponseEntity<MeetingDetailDTO> joinUpdate(@RequestBody @Valid JoinUpdateDTO dto){
        return ResponseEntity.ok(meetingService.updateJoin(dto));
    }

    @GetMapping("/join")
    public ResponseEntity<JoinDTO> join(@RequestParam int meetingId){
        return ResponseEntity.ok(meetingService.joinMeeting(meetingId));
    }

    @GetMapping("/header-info")
    public ResponseEntity<MeetingHeaderInfoDTO> getHeaderInfo(@RequestParam int meetingId){
        return ResponseEntity.ok(meetingService.getHeaderInfo(meetingId));
    }

}
