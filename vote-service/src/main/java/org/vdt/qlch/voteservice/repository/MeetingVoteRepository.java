package org.vdt.qlch.voteservice.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.voteservice.model.MeetingVote;

import java.util.List;

@Repository
public interface MeetingVoteRepository extends JpaRepository<MeetingVote, Integer> {

    List<MeetingVote> findAllByMeetingIdOrderByStartTimeDesc(int meetingId);

    @Cacheable(value = "meeting-vote", key = "#id")
    MeetingVote findById(int id);

}
