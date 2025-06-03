package org.vdt.qlch.meetingservice.service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.vdt.qlch.meetingservice.dto.redis.OnlineUserDTO;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final String MEETING_ONLINE_USERS_PREFIX = "meeting-online-users-";

    public void addUserOnline(OnlineUserDTO user, int meetingId){
        List<OnlineUserDTO> onlineUsers = getUserOnlineList(meetingId);
        onlineUsers.add(user);
        try{
            redisTemplate.opsForValue().set(MEETING_ONLINE_USERS_PREFIX + meetingId,
                    onlineUsers, Duration.ofMinutes(120));
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
    }

    public void removeUserOnline(String userId, int meetingId){
        List<OnlineUserDTO> onlineUsers = getUserOnlineList(meetingId);
        onlineUsers = onlineUsers.stream()
                .filter(u -> !u.id().equals(userId))
                .toList();
        try{
            redisTemplate.opsForValue().set(MEETING_ONLINE_USERS_PREFIX + meetingId,
                    onlineUsers, Duration.ofMinutes(120));
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
    }

    public List<OnlineUserDTO> getUserOnlineList(int meetingId){
        try{
            List<OnlineUserDTO> onlineUsers = (List<OnlineUserDTO>) redisTemplate.opsForValue()
                    .get(MEETING_ONLINE_USERS_PREFIX + meetingId);
            if(onlineUsers == null){
                onlineUsers = new ArrayList<>();
            }
            return onlineUsers;
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
        return new ArrayList<>();
    }

}
