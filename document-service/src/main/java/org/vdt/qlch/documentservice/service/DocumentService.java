package org.vdt.qlch.documentservice.service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.vdt.commonlib.dto.DocumentDTO;
import org.vdt.commonlib.dto.RecordExistDTO;
import org.vdt.commonlib.exception.ServerException;
import org.vdt.commonlib.utils.AuthenticationUtil;
import org.vdt.qlch.documentservice.model.Document;
import org.vdt.qlch.documentservice.repository.DocumentRepository;
import org.vdt.qlch.documentservice.utils.Constants;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    private final Cloudinary cloudinary;

    @Transactional
    public DocumentDTO upload(MultipartFile file, boolean save) {
        try{
            String url = cloudinary.uploader().upload(file.getBytes(),
                        Map.of("public_id", UUID.randomUUID().toString(),
                                "resource_type", "raw"))
                    .get("url").toString();
            Document document = Document.builder()
                    .url(url)
                    .name(file.getOriginalFilename())
                    .size((int)file.getSize())
                    .createdBy(save ? AuthenticationUtil.extractUserId() : null)
                    .build();
            documentRepository.save(document);
            return DocumentDTO.builder()
                    .id(document.getId())
                    .url(url)
                    .name(document.getName())
                    .size(document.getSize())
                    .build();
        }
        catch (Exception e){
            throw new ServerException(Constants.ErrorCode.UPLOAD_FAILED);
        }
    }

    public List<DocumentDTO> getList(List<Integer> documentIds) {
        List<Document> documents = documentRepository.findAllById(documentIds);
        return documents.stream()
                .map(d -> DocumentDTO.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .url(d.getUrl())
                        .size(d.getSize())
                        .build())
                .toList();
    }

    public RecordExistDTO checkExistById(List<Integer> documentIds) {
        Set<Integer> idsSet = new HashSet<>(documentIds);
        int cnt = documentRepository.countByIdIn(idsSet);
        return new RecordExistDTO(cnt == idsSet.size());
    }


}