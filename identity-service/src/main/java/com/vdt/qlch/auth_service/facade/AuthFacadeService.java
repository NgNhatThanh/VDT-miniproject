package com.vdt.qlch.auth_service.facade;

import com.vdt.qlch.auth_service.dto.TokenVerificationResponse;
import com.vdt.qlch.auth_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFacadeService {

    private final JwtService jwtService;

    public TokenVerificationResponse verifyToken(String token){
        boolean isValid = jwtService.isTokenValid(token);
        return TokenVerificationResponse.builder()
                .valid(isValid)
                .build();
    }

}
