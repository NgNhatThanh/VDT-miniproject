package org.vdt.qlch.meetingservice.service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdt.commonlib.dto.UserDTO;
import org.vdt.qlch.meetingservice.dto.redis.OnlineUserDTO;
import org.vdt.qlch.meetingservice.dto.response.ParticipantDTO;
import org.vdt.qlch.meetingservice.dto.response.RoleDTO;
import org.vdt.qlch.meetingservice.model.MeetingJoin;
import org.vdt.qlch.meetingservice.repository.MeetingJoinRepository;
import org.vdt.qlch.meetingservice.service.UserService;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final String MEETING_ONLINE_USERS_PREFIX = "meeting-online-users-";

    private final MeetingJoinRepository joinRepository;
    private final UserService userService;

    @Transactional
    public void addUserOnline(OnlineUserDTO user, int meetingId){
        try{
            Set<OnlineUserDTO> onlineUsers = getUserOnlineList(meetingId);
            onlineUsers.add(user);
            redisTemplate.opsForValue().set(MEETING_ONLINE_USERS_PREFIX + meetingId,
                    onlineUsers, Duration.ofMinutes(120));
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
    }

    @Transactional
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

    @Cacheable(value = "meeting-participants", key = "#meetingId")
    public List<ParticipantDTO> getListParticipants(int meetingId) {
        List<MeetingJoin> joins = joinRepository.findAllByMeeting_Id(meetingId);
        List<String> userIds = joins.stream()
                .map(MeetingJoin::getUserId)
                .toList();
        List<UserDTO> users = userService.getListByIds(userIds);
        Map<String, UserDTO> userMap = users.stream()
                .collect(Collectors.toMap(UserDTO::id, Function.identity()));
        return joins.stream()
                .map(j -> ParticipantDTO.builder()
                        .joinId(j.getId())
                        .fullName(userMap.get(j.getUserId()).fullName())
                        .picture(userMap.get(j.getUserId()).picture())
                        .status(j.getStatus())
                        .roles(j.getRoles().stream().map(RoleDTO::from).toList())
                        .build())
                .toList();
    }

}
