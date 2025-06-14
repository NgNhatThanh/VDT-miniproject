package org.vdt.qlch.meetingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.meetingservice.model.Meeting;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Integer> {
}
