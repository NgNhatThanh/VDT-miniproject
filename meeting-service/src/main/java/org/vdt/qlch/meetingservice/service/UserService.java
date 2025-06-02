package org.vdt.qlch.meetingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.vdt.commonlib.dto.RecordExistDTO;
import org.vdt.qlch.meetingservice.config.ServiceUrlConfig;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final RestClient restClient;

    private final ServiceUrlConfig serviceUrlConfig;

    public boolean checkExistById(List<String> userIds){
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromUriString(serviceUrlConfig.user())
                .path("/exist")
                .buildAndExpand()
                .toUri();
        RecordExistDTO res = restClient.post()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .body(userIds)
                .retrieve()
                .body(RecordExistDTO.class);
        return res != null && res.exist();
    }

}
