package org.vdt.qlch.voteservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.voteservice.model.MeetingVote;
import org.vdt.qlch.voteservice.model.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer> {
    boolean existsByCreatedByAndMeetingVote(String createdBy, MeetingVote meetingVote);
}
