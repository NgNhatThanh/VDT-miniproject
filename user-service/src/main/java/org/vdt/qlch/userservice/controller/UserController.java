package org.vdt.qlch.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.vdt.qlch.userservice.dto.UserExistByIdDTO;
import org.vdt.qlch.userservice.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/exist")
    public ResponseEntity<UserExistByIdDTO> checkExistById(@RequestBody List<String> userIds){
        return ResponseEntity.ok(userService.checkExistsById(userIds));
    }

}
