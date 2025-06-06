package org.vdt.qlch.meetinghistoryservice.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtDecoder jwtDecoder;

    public boolean validateToken(String token) {
        try{
            jwtDecoder.decode(token);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public String getUserId(String token){
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

}
