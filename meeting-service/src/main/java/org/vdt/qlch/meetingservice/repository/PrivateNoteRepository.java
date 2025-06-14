package org.vdt.qlch.meetingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.meetingservice.model.MeetingPrivateNote;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateNoteRepository extends JpaRepository<MeetingPrivateNote, Integer> {
    List<MeetingPrivateNote> findAllByMeeting_IdAndCreatedBy(int meetingId, String createdBy);

    Optional<MeetingPrivateNote> findByIdAndCreatedBy(int id, String createdBy);
}
