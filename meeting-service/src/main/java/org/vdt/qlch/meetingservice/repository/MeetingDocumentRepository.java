package org.vdt.qlch.meetingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.meetingservice.model.MeetingDocument;
import org.vdt.qlch.meetingservice.model.enums.DocumentStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingDocumentRepository extends JpaRepository<MeetingDocument, Integer> {

    List<MeetingDocument> findAllByMeeting_IdAndStatus(int meetingId, DocumentStatus status);

    Optional<MeetingDocument> findByIdAndMeeting_Id(int id, int meetingId);

}
