package org.vdt.qlch.meetingservice.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.vdt.commonlib.dto.DocumentDTO;
import org.vdt.commonlib.dto.MeetingHistoryMessage;
import org.vdt.commonlib.dto.UserDTO;
import org.vdt.commonlib.exception.BadRequestException;
import org.vdt.commonlib.model.MeetingHistoryType;
import org.vdt.commonlib.utils.AuthenticationUtil;
import org.vdt.commonlib.utils.JwtUtil;
import org.vdt.qlch.meetingservice.dto.request.UpdateDocumentStatusDTO;
import org.vdt.qlch.meetingservice.dto.response.DocumentDTOForApprovement;
import org.vdt.qlch.meetingservice.model.Meeting;
import org.vdt.qlch.meetingservice.model.MeetingDocument;
import org.vdt.qlch.meetingservice.model.enums.DocumentStatus;
import org.vdt.qlch.meetingservice.producer.MeetingHistoryProducer;
import org.vdt.qlch.meetingservice.repository.MeetingDocumentRepository;
import org.vdt.qlch.meetingservice.repository.MeetingRepository;
import org.vdt.qlch.meetingservice.utils.Constants;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MeetingDocumentService {

    private final DocumentService documentService;

    private final MeetingDocumentRepository meetingDocumentRepository;

    private final MeetingRepository meetingRepository;

    private final MeetingHistoryProducer producer;

    private final JwtUtil jwtUtil;

    private final UserService userService;

    @Cacheable(value = "meeting-documents", key = "#meetingId")
    public List<DocumentDTO> getList(int meetingId) {
        List<MeetingDocument> documentList = meetingDocumentRepository.findAllByMeeting_IdAndStatus(meetingId,
                DocumentStatus.APPROVED);
        List<Integer> documentIds = documentList.stream().map(MeetingDocument::getDocumentId).toList();
        return documentService.getList(documentIds);
    }

    public void addToMeeting(int meetingId, int documentId) {
        boolean exists = documentService.checkExistById(List.of(documentId));
        if (!exists) {
            throw new BadRequestException(Constants.ErrorCode.DOCUMENT_NOT_FOUND);
        }
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BadRequestException(Constants.ErrorCode.MEETING_NOT_FOUND));
        MeetingDocument meetingDocument = MeetingDocument.builder()
                .meeting(meeting)
                .documentId(documentId)
                .status(DocumentStatus.PENDING)
                .createdBy(AuthenticationUtil.extractUserId())
                .build();
        meetingDocumentRepository.save(meetingDocument);
        UserDTO user = jwtUtil.extractUser(AuthenticationUtil.extractJwt());
        producer.send(MeetingHistoryMessage.builder()
                        .meetingId(meetingId)
                        .type(MeetingHistoryType.DOCUMENT_UPLOADED)
                        .content(String.format("%s đã đề xuất tài liệu mới", user.fullName()))
                .build());
    }

    public List<DocumentDTOForApprovement> getForApprovement(int meetingId) {
        List<MeetingDocument> needToApproveList = meetingDocumentRepository.findAllByMeeting_IdAndStatus(meetingId,
                DocumentStatus.PENDING);
        List<Integer> documentIds = needToApproveList.stream().map(MeetingDocument::getDocumentId).toList();
        List<DocumentDTO> documentDTOS = documentService.getList(documentIds);
        List<String> userIds = needToApproveList.stream()
                .map(MeetingDocument::getCreatedBy)
                .toList();
        List<UserDTO> users = userService.getListByIds(userIds);
        return needToApproveList.stream()
                .map(doc -> {
                    DocumentDTO detail = documentDTOS.stream()
                            .dropWhile(d -> d.id() != doc.getDocumentId())
                            .findFirst()
                            .orElse(null);
                    String userFullName = Objects.requireNonNull(users.stream()
                                    .dropWhile(u -> !u.id().equals(doc.getCreatedBy()))
                                    .findFirst()
                                    .orElse(null))
                            .fullName();
                    return new DocumentDTOForApprovement(doc.getId(), detail, userFullName);
                })
                .toList();
    }

    @CacheEvict(value = "meeting-documents", key = "#dto.meetingId()")
    public void updateStatus(@Valid UpdateDocumentStatusDTO dto) {
        DocumentStatus status;
        try{
            status = DocumentStatus.fromString(dto.status());
            if(status == DocumentStatus.PENDING)
                throw new IllegalArgumentException();
        }
        catch (IllegalArgumentException e){
            throw new BadRequestException(org.vdt.commonlib.utils.Constants.ErrorCode.INVALID_ENUM_VALUE);
        }
        MeetingDocument doc = meetingDocumentRepository.findByIdAndMeeting_Id(dto.meetingDocumentId(),
                        dto.meetingId())
                .orElseThrow(() -> new BadRequestException(Constants.ErrorCode.DOCUMENT_NOT_FOUND));
        doc.setStatus(status);
        String userId = AuthenticationUtil.extractUserId();
        doc.setApprovedBy(userId);
        meetingDocumentRepository.save(doc);
        UserDTO user = userService.getById(doc.getCreatedBy());
        String format = "Tài liệu của %s " + (status == DocumentStatus.APPROVED ? "đã được phê duyệt" : "bị từ chối");
        producer.send(MeetingHistoryMessage.builder()
                        .meetingId(dto.meetingId())
                        .type(status == DocumentStatus.APPROVED ? MeetingHistoryType.DOCUMENT_APPROVED
                                : MeetingHistoryType.DOCUMENT_REJECTED)
                        .content(String.format(format, user.fullName()))
                .build());
    }
}
