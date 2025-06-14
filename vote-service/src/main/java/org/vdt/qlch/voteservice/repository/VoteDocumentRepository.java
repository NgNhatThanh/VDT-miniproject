package org.vdt.qlch.voteservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.voteservice.model.VoteDocument;

@Repository
public interface VoteDocumentRepository extends JpaRepository<VoteDocument, Integer> {
}
