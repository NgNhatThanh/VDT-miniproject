package org.vdt.qlch.documentservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.vdt.commonlib.dto.DocumentDTO;
import org.vdt.commonlib.dto.RecordExistDTO;
import org.vdt.qlch.documentservice.service.DocumentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/exist")
    public ResponseEntity<RecordExistDTO> checkExistById(@RequestBody List<Integer> documentIds) {
        RecordExistDTO res = documentService.checkExistById(documentIds);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/get-list-documents")
    public ResponseEntity<List<DocumentDTO>> getListDocuments(@RequestBody List<Integer> documentIds) {
        return ResponseEntity.ok(documentService.getList(documentIds));
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentDTO> upload(@RequestParam("file") MultipartFile file,
                                              @RequestParam(value = "save",
                                                      required = false,
                                                      defaultValue = "true") boolean save) {
        DocumentDTO res = documentService.upload(file, save);
        return ResponseEntity.ok(res);
    }

}
