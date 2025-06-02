package org.vdt.qlch.meetingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.meetingservice.model.MeetingRole;

import java.util.List;

@Repository
public interface MeetingRoleRepository extends JpaRepository<MeetingRole, Integer> {

    @Query(nativeQuery = true,
            value = "select id, name, description from meeting_roles")
    List<MeetingRole> findAllWithBasicInfo();

}
