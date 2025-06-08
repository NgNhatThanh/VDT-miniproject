package org.vdt.qlch.meetinghistoryservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.vdt.commonlib.dto.MeetingLeaveMessage;
import org.vdt.commonlib.utils.Constants;
import org.vdt.commonlib.utils.JwtUtil;
import org.vdt.qlch.meetinghistoryservice.dto.ConnectionInfo;
import org.vdt.qlch.meetinghistoryservice.producer.UserLeaveProducer;
import org.vdt.qlch.meetinghistoryservice.utils.KafkaUtils;

@Configuration
@RequiredArgsConstructor
public class CustomWSListener {

    private final JwtUtil jwtUtil;

    private final UserLeaveProducer producer;

    private final KafkaUtils kafkaUtils;

    @EventListener
    public void handleConnectEvent(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        int meetingId = Integer.parseInt((String)headerAccessor.getSessionAttributes().get("meetingId"));
        String token = (String) headerAccessor.getSessionAttributes().get("token");
        String userId = jwtUtil.extractUserId(token);
        String sessionId = headerAccessor.getSessionId();
        WebSocketConfig.connectionInfoMap.put(sessionId, new ConnectionInfo(sessionId, userId, meetingId));
        WebSocketConfig.meetingConnectCount.put(meetingId,
                WebSocketConfig.meetingConnectCount.getOrDefault(meetingId, 0) + 1);
        if(WebSocketConfig.meetingConnectCount.get(meetingId) == 1){
            kafkaUtils.subscribeTopics(String.format(Constants.MEETING_WS_TOPIC_FORMAT, meetingId));
        }
    }

    @EventListener
    public void handleCloseConnection(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        ConnectionInfo connectionInfo = WebSocketConfig.connectionInfoMap.get(sessionId);
        int meetingId = connectionInfo.meetingId();
        String userId = connectionInfo.userId();
        WebSocketConfig.connectionInfoMap.remove(sessionId);
        WebSocketConfig.meetingConnectCount.put(meetingId, WebSocketConfig.meetingConnectCount.get(meetingId) - 1);
        producer.send(new MeetingLeaveMessage(userId, meetingId));
        if(WebSocketConfig.meetingConnectCount.get(meetingId) == 0) {
            kafkaUtils.unsubscribe(String.format(Constants.MEETING_WS_TOPIC_FORMAT, meetingId));
        }
    }

}
