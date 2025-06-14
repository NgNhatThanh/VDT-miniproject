package org.vdt.qlch.meetingservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vdt.commonlib.dto.RecordExistDTO;
import org.vdt.qlch.meetingservice.dto.request.CreateMeetingDTO;
import org.vdt.qlch.meetingservice.dto.request.CreateNoteDTO;
import org.vdt.qlch.meetingservice.dto.request.JoinUpdateDTO;
import org.vdt.qlch.meetingservice.dto.request.UpdateNoteDTO;
import org.vdt.qlch.meetingservice.dto.response.*;
import org.vdt.qlch.meetingservice.service.MeetingService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/check-join")
    public ResponseEntity<RecordExistDTO> checkJoin(@RequestParam int meetingId){
        return ResponseEntity.ok(meetingService.checkJoin(meetingId));
    }

    @GetMapping("/join")
    public ResponseEntity<JoinDTO> join(@RequestParam int meetingId){
        return ResponseEntity.ok(meetingService.joinMeeting(meetingId));
    }

    @GetMapping("/header-info")
    public ResponseEntity<MeetingHeaderInfoDTO> getHeaderInfo(@RequestParam int meetingId){
        return ResponseEntity.ok(meetingService.getHeaderInfo(meetingId));
    }

    @GetMapping("/get-list-note")
    public ResponseEntity<List<NoteDTO>> getList(@RequestParam int meetingId){
        return ResponseEntity.ok(meetingService.getListNote(meetingId));
    }

    @PostMapping("/add-note")
    public ResponseEntity<NoteDTO> addNote(@RequestBody @Valid CreateNoteDTO dto){
        return ResponseEntity.ok(meetingService.addNote(dto));
    }

    @PostMapping("/update-note")
    public ResponseEntity<NoteDTO> updateNote(@RequestBody @Valid UpdateNoteDTO dto){
        return ResponseEntity.ok(meetingService.updateNote(dto));
    }

    @PostMapping("/delete-note")
    public ResponseEntity<Map<String, String>> deleteNote(@RequestParam int noteId){
        meetingService.deleteNote(noteId);
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    @GetMapping("/get-list-participants")
    public ResponseEntity<List<ParticipantDTO>> getListParticipants(@RequestParam int meetingId){
        return ResponseEntity.ok(meetingService.getListParticipants(meetingId));
    }

}
