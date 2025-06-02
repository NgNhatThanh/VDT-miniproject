package org.vdt.qlch.meetingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vdt.qlch.meetingservice.dto.response.RoleDTO;
import org.vdt.qlch.meetingservice.model.MeetingRole;
import org.vdt.qlch.meetingservice.repository.MeetingRoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingRoleService {

    private final MeetingRoleRepository meetingRoleRepository;

    public List<RoleDTO> getAll() {
        List<MeetingRole> meetingRoles = meetingRoleRepository.findAll();
        return meetingRoles.stream().map(RoleDTO::from).toList();
    }
}
