package org.vdt.qlch.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.vdt.commonlib.dto.RecordExistDTO;
import org.vdt.qlch.userservice.dto.UserDTO;
import org.vdt.qlch.userservice.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/exist")
    public ResponseEntity<RecordExistDTO> checkExistById(@RequestBody List<String> userIds){
        return ResponseEntity.ok(userService.checkExistsById(userIds));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAll(){
        return ResponseEntity.ok(userService.getAll());
    }

}
