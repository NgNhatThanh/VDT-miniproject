package org.vdt.qlch.meetinghistoryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.meetinghistoryservice.model.MeetingHistory;

@Repository
public interface MeetingHistoryRepository extends JpaRepository<MeetingHistory, Integer> {
}
