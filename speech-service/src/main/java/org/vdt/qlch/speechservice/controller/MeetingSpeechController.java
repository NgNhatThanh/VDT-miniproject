package org.vdt.qlch.speechservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vdt.qlch.speechservice.dto.request.SpeechRegisterDTO;
import org.vdt.qlch.speechservice.dto.request.UpdateSpeechDTO;
import org.vdt.qlch.speechservice.dto.response.SpeechDTO;
import org.vdt.qlch.speechservice.service.MeetingSpeechService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MeetingSpeechController {

    private final MeetingSpeechService meetingSpeechService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> speechRegister(@RequestBody @Valid SpeechRegisterDTO dto){
        meetingSpeechService.register(dto);
        return ResponseEntity.ok().body(Map.of("status", "success"));
    }

    @GetMapping("/get-list")
    public ResponseEntity<List<SpeechDTO>> getList(@RequestParam int meetingId){
        return ResponseEntity.ok(meetingSpeechService.getList(meetingId));
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> updateSpeech(@RequestBody @Valid UpdateSpeechDTO dto){
        meetingSpeechService.updateSpeech(dto);
        return ResponseEntity.ok().body(Map.of("status", "success"));
    }

}
