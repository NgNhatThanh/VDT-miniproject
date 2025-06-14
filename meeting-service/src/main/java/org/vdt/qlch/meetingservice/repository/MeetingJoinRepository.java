package org.vdt.qlch.meetingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.meetingservice.dto.response.MeetingCardDTO;
import org.vdt.qlch.meetingservice.model.MeetingJoin;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MeetingJoinRepository extends JpaRepository<MeetingJoin, Integer> {

    @Query(nativeQuery = true,
        value = "select j.id as joinId, m.id as meetingId, m.title, m.description, l.name as location, m.start_time, m.end_time, j.status \n" +
                "from user_join_meeting j\n" +
                "join meetings m\n" +
                "on j.meeting_id = m.id\n" +
                "join meeting_locations l\n" +
                "on m.location_id = l.id\n" +
                "where j.user_id = ?1\n" +
                "and (date(m.start_time) between ?2 and ?3\n" +
                "or date(m.end_time) between ?2 and ?3)")
    List<MeetingCardDTO> findAllByUserWithinDate(String userId, LocalDate startDate, LocalDate endDate);

    @Query(nativeQuery = true,
            value = "select * from user_join_meeting where user_id = ?1 and meeting_id = ?2")
    MeetingJoin findByUserIdAndMeetingId(String userId, int meetingId);

    List<MeetingJoin> findAllByMeeting_Id(int meetingId);
}
