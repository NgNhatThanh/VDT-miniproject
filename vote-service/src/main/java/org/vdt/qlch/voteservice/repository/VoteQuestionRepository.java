package org.vdt.qlch.voteservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.voteservice.model.VoteQuestion;

@Repository
public interface VoteQuestionRepository extends JpaRepository<VoteQuestion, Integer> {
}
