package org.vdt.qlch.meetinghistoryservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.vdt.commonlib.utils.JwtUtil;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        UriComponents uriComponents = UriComponentsBuilder.fromUri(request.getURI()).build();
        String token = uriComponents.getQueryParams().getFirst("token");
        String meetingId = uriComponents.getQueryParams().getFirst("meetingId");
        
        if (token != null && jwtUtil.validateToken(token)) {
            attributes.put("token", token);
            if (meetingId != null) {
                attributes.put("meetingId", meetingId);
            }
            return true;
        }
        System.out.println("Token: " + token);
        System.out.println("MeetingId: " + meetingId);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception ex) {
    }
}

