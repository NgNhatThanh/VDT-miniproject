package org.vdt.qlch.voteservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vdt.commonlib.utils.AuthenticationUtil;
import org.vdt.qlch.voteservice.dto.request.CreateVoteDTO;
import org.vdt.qlch.voteservice.dto.request.VoteDTO;
import org.vdt.qlch.voteservice.dto.response.MeetingVoteDTO;
import org.vdt.qlch.voteservice.dto.response.MeetingVoteDetailDTO;
import org.vdt.qlch.voteservice.dto.response.MeetingVoteStatusDTO;
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
        String userId = AuthenticationUtil.extractUserId();
        return ResponseEntity.ok(meetingVoteService.getMeetingVoteListForUser(userId, meetingId));
    }

    @GetMapping("/get-vote-for-selection")
    public ResponseEntity<MeetingVoteDetailDTO> getForSelection(@RequestParam int meetingVoteId){
        return ResponseEntity.ok(meetingVoteService.getForSelection(meetingVoteId));
    }

    @PostMapping("/vote")
    public ResponseEntity<Map<String, String>> vote(@RequestBody @Valid VoteDTO dto){
        String userId = AuthenticationUtil.extractUserId();
        meetingVoteService.vote(userId, dto);
        return ResponseEntity.ok().body(Map.of("message", "Vote voted"));
    }

    @GetMapping("/status")
    public ResponseEntity<MeetingVoteStatusDTO> getStatus(@RequestParam int meetingVoteId){
        return ResponseEntity.ok(meetingVoteService.getStatus(meetingVoteId));
    }


}
