package org.vdt.qlch.meetingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.meetingservice.model.MeetingDocument;

@Repository
public interface MeetingDocumentRepository extends JpaRepository<MeetingDocument, Integer> {
}
