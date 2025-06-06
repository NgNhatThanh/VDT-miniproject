package org.vdt.qlch.meetinghistoryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.meetinghistoryservice.model.MeetingHistory;

import java.util.List;

@Repository
public interface MeetingHistoryRepository extends JpaRepository<MeetingHistory, Integer> {

    @Query(nativeQuery = true,
        value = "select * from meeting_histories\n" +
                "where meeting_id = ?1\n" +
                "order by created_at desc\n"+
                "limit ?2\n" +
                "offset ?3")
    List<MeetingHistory> getList(int meetingId, int limit, int offset);

}
