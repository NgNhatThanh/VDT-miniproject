package org.vdt.qlch.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getById(@PathVariable String userId){
        return ResponseEntity.ok(userService.getById(userId));
    }

    @PostMapping("/get-list")
    public ResponseEntity<List<UserDTO>> getList(@RequestBody List<String> userIds){
        return ResponseEntity.ok(userService.getList(userIds));
    }

}
