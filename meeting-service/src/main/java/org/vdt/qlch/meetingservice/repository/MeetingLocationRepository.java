package org.vdt.qlch.meetingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.meetingservice.model.MeetingLocation;

@Repository
public interface MeetingLocationRepository extends JpaRepository<MeetingLocation, Integer> {
    boolean existsByNameIgnoreCase(String name);
}
