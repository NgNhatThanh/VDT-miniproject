package org.vdt.qlch.meetingservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vdt.qlch.meetingservice.dto.response.LocationDTO;
import org.vdt.qlch.meetingservice.service.MeetingLocationService;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class MeetingLocationController {

    private final MeetingLocationService meetingLocationService;

    @PostMapping("/add")
    public ResponseEntity<LocationDTO> addLocation(@RequestBody LocationDTO locationDTO) {
        return ResponseEntity.ok(meetingLocationService.addLocation(locationDTO));
    }

}
