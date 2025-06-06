package org.vdt.qlch.meetingservice.service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.vdt.qlch.meetingservice.dto.redis.OnlineUserDTO;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final String MEETING_ONLINE_USERS_PREFIX = "meeting-online-users-";

    public void addUserOnline(OnlineUserDTO user, int meetingId){
        Set<OnlineUserDTO> onlineUsers = getUserOnlineList(meetingId);
        onlineUsers.add(user);
        try{
            redisTemplate.opsForValue().set(MEETING_ONLINE_USERS_PREFIX + meetingId,
                    onlineUsers, Duration.ofMinutes(120));
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
    }

    public OnlineUserDTO removeUserOnline(String userId, int meetingId){
        Set<OnlineUserDTO> onlineUsers = getUserOnlineList(meetingId);
        OnlineUserDTO user = onlineUsers.stream()
                .filter(u -> u.id().equals(userId))
                .findFirst().orElse(null);
        onlineUsers = onlineUsers.stream()
                .filter(u -> !u.id().equals(userId))
                .collect(Collectors.toSet());
        try{
            redisTemplate.opsForValue().set(MEETING_ONLINE_USERS_PREFIX + meetingId,
                    onlineUsers, Duration.ofMinutes(120));
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
        return user;
    }

    public Set<OnlineUserDTO> getUserOnlineList(int meetingId){
        try{
            Set<OnlineUserDTO> onlineUsers = (Set<OnlineUserDTO>) redisTemplate.opsForValue()
                    .get(MEETING_ONLINE_USERS_PREFIX + meetingId);
            if(onlineUsers == null){
                onlineUsers = new HashSet<>();
            }
            return onlineUsers;
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
        return new HashSet<>();
    }

}
