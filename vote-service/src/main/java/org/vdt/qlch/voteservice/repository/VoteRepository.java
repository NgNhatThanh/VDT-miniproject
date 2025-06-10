package org.vdt.qlch.voteservice.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.voteservice.model.Vote;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer> {

    @Cacheable(value = "meeting-vote-voted", key = "#createdBy+'-'+#meetingVoteId")
    boolean existsByCreatedByAndMeetingVote_Id(String createdBy, int meetingVoteId);

    Vote findByCreatedByAndMeetingVote_Id(String createdBy, int meetingVoteId);

    List<Vote> findAllByMeetingVote_Id(int meetingVoteId);
}
