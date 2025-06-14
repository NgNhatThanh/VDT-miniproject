package org.vdt.qlch.meetingservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vdt.qlch.meetingservice.dto.response.RoleDTO;
import org.vdt.qlch.meetingservice.service.MeetingRoleService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/role")
public class MeetingRoleController {

    private final MeetingRoleService meetingRoleService;

    @GetMapping("/all")
    public ResponseEntity<List<RoleDTO>> getAllRoles(){
        return ResponseEntity.ok(meetingRoleService.getAll());
    }

}
