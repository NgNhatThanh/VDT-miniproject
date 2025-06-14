package org.vdt.qlch.documentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.documentservice.model.Document;

import java.util.Set;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

    @Query("select count(*) from Document where id in ?1")
    int countByIdIn(Set<Integer> idsSet);

}
