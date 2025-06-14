package org.vdt.qlch.voteservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.vdt.commonlib.dto.UserDTO;
import org.vdt.qlch.voteservice.config.ServiceUrlConfig;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final RestClient restClient;

    private final ServiceUrlConfig serviceUrlConfig;

    public List<UserDTO> getListByIds(List<String> userIds){
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromUriString(serviceUrlConfig.user())
                .path("/get-list")
                .buildAndExpand()
                .toUri();
        List<UserDTO> res = restClient.post()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .body(userIds)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return res;
    }

}
