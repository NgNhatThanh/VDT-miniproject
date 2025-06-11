package org.vdt.qlch.speechservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.vdt.commonlib.dto.UserDTO;
import org.vdt.commonlib.exception.BadRequestException;
import org.vdt.commonlib.exception.NotFoundException;
import org.vdt.qlch.speechservice.config.ServiceUrlConfig;
import org.vdt.qlch.speechservice.utils.Constants;

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

    public UserDTO getById(String userId){
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromUriString(serviceUrlConfig.user())
                .path("/" + userId)
                .buildAndExpand()
                .toUri();
        try{
            var res = restClient.get()
                    .uri(url)
                    .headers(h -> h.setBearerAuth(jwt))
                    .retrieve()
                    .body(UserDTO.class);
            return res;
        }
        catch(HttpClientErrorException e){
            throw new BadRequestException(Constants.ErrorCode.SPEECH_NOT_FOUND);
        }
    }

}
