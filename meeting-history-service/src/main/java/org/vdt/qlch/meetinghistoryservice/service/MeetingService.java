package org.vdt.qlch.meetinghistoryservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.vdt.commonlib.dto.RecordExistDTO;
import org.vdt.qlch.meetinghistoryservice.config.ServiceUrlConfig;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final RestClient restClient;

    private final ServiceUrlConfig serviceUrlConfig;

    public boolean checkJoin(int meetingId){
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromUriString(serviceUrlConfig.meeting())
                .path("/check-join")
                .queryParam("meetingId", meetingId)
                .buildAndExpand()
                .toUri();
        RecordExistDTO res = restClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .retrieve()
                .body(RecordExistDTO.class);
        return res != null && res.exist();
    }

}
