package org.vdt.commonlib.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.vdt.commonlib.dto.UserDTO;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtDecoder jwtDecoder;

    public String extractUserId(String token){
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

    public UserDTO extractUser(String token){
        Jwt jwt = jwtDecoder.decode(token);
        String lastName = jwt.getClaimAsString("family_name");
        String firstName = jwt.getClaimAsString("given_name");
        return UserDTO.builder()
                .id(jwt.getSubject())
                .username(jwt.getClaimAsString("preferred_username"))
                .fullName(lastName + " " + firstName)
                .email(jwt.getClaimAsString("email"))
                .picture(jwt.getClaimAsString("picture"))
                .build();
    }

    public boolean validateToken(String token) {
        try{
            jwtDecoder.decode(token);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

}
