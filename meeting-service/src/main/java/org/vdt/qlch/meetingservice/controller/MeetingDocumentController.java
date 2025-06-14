package org.vdt.qlch.meetingservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vdt.commonlib.dto.DocumentDTO;
import org.vdt.qlch.meetingservice.dto.request.UpdateDocumentStatusDTO;
import org.vdt.qlch.meetingservice.dto.response.DocumentDTOForApprovement;
import org.vdt.qlch.meetingservice.service.MeetingDocumentService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document")
public class MeetingDocumentController {

    private final MeetingDocumentService meetingDocumentService;

    @GetMapping("/get-list")
    public ResponseEntity<List<DocumentDTO>> getListDocuments(@RequestParam int meetingId){
        return ResponseEntity.ok(meetingDocumentService.getList(meetingId));
    }

    @GetMapping("/get-for-approvement")
    public ResponseEntity<List<DocumentDTOForApprovement>> getForApprovement(@RequestParam int meetingId){
        return ResponseEntity.ok(meetingDocumentService.getForApprovement(meetingId));
    }

    @PostMapping("/add-to-meeting")
    public ResponseEntity<Map<String, String>> addToMeeting(@RequestParam int meetingId,
                                                            @RequestParam int documentId){
        meetingDocumentService.addToMeeting(meetingId, documentId);
        return ResponseEntity.ok(Map.of("message", "success"));
    }

    @PostMapping("/update-status")
    public ResponseEntity<Map<String, String>> updateDocumentStatus(@RequestBody @Valid UpdateDocumentStatusDTO dto){
        meetingDocumentService.updateStatus(dto);
        return ResponseEntity.ok(Map.of("message", "success"));
    }


}
