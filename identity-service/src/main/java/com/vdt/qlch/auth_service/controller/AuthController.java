package com.vdt.qlch.auth_service.controller;

import com.vdt.qlch.auth_service.dto.APIResponse;
import com.vdt.qlch.auth_service.dto.TokenVerificationResponse;
import com.vdt.qlch.auth_service.facade.AuthFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthFacadeService authService;

    @PostMapping("/verify-token")
    public APIResponse<TokenVerificationResponse> verifyToken(@RequestHeader(name = "Authorization") String authHeader){
        String token = authHeader.replace("Bearer ", "");
        var result = authService.verifyToken(token);
        return APIResponse.<TokenVerificationResponse>builder()
                .code(HttpStatus.OK.value())
                .data(result)
                .build();
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }

}
