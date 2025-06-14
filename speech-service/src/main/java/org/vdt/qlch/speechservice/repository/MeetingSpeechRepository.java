package org.vdt.qlch.speechservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.speechservice.model.MeetingSpeech;

import java.util.List;

@Repository
public interface MeetingSpeechRepository extends JpaRepository<MeetingSpeech, Integer> {

    List<MeetingSpeech> getAllByMeetingIdOrderByCreatedAt(int meetingId);

}
