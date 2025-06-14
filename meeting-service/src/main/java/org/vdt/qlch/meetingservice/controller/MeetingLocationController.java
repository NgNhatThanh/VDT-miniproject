package org.vdt.qlch.meetingservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vdt.qlch.meetingservice.dto.response.LocationDTO;
import org.vdt.qlch.meetingservice.service.MeetingLocationService;

import java.util.List;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class MeetingLocationController {

    private final MeetingLocationService meetingLocationService;

    @PostMapping("/add")
    public ResponseEntity<LocationDTO> addLocation(@RequestBody LocationDTO locationDTO) {
        return ResponseEntity.ok(meetingLocationService.addLocation(locationDTO));
    }

    @GetMapping("/all")
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        return ResponseEntity.ok(meetingLocationService.getAll());
    }

}
