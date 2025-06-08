package org.vdt.qlch.voteservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vdt.qlch.voteservice.dto.CreateVoteDTO;
import org.vdt.qlch.voteservice.dto.MeetingVoteDTO;
import org.vdt.qlch.voteservice.service.MeetingVoteService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MeetingVoteController {

    private final MeetingVoteService meetingVoteService;

    @PostMapping("/create-vote")
    public ResponseEntity<Map<String, String>> createVote(@RequestBody @Valid CreateVoteDTO dto){
        meetingVoteService.createVote(dto);
        return ResponseEntity.ok().body(Map.of("message", "Vote created"));
    }

    @GetMapping("/get-list")
    public ResponseEntity<List<MeetingVoteDTO>> getList(@RequestParam int meetingId){
        return ResponseEntity.ok(meetingVoteService.getList(meetingId));
    }

}
