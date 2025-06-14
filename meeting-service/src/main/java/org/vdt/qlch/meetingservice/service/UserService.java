package org.vdt.qlch.meetingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.vdt.commonlib.dto.RecordExistDTO;
import org.vdt.commonlib.dto.UserDTO;
import org.vdt.commonlib.exception.BadRequestException;
import org.vdt.commonlib.exception.NotFoundException;
import org.vdt.qlch.meetingservice.config.ServiceUrlConfig;
import org.vdt.qlch.meetingservice.utils.Constants;

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

    public UserDTO getById(String userId){
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromUriString(serviceUrlConfig.user())
                .path("/" + userId)
                .buildAndExpand()
                .toUri();
        try{
            ResponseEntity<UserDTO> res = restClient.get()
                    .uri(url)
                    .headers(h -> h.setBearerAuth(jwt))
                    .retrieve()
                    .toEntity(UserDTO.class);
            return res.getBody();
        }
        catch(HttpClientErrorException e){
            if(e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new NotFoundException(Constants.ErrorCode.PARTICIPANT_NOT_FOUND);
            else
                throw new BadRequestException(Constants.ErrorCode.PARTICIPANT_NOT_FOUND);
        }
    }

    public List<org.vdt.commonlib.dto.UserDTO> getListByIds(List<String> userIds){
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromUriString(serviceUrlConfig.user())
                .path("/get-list")
                .buildAndExpand()
                .toUri();
        List<org.vdt.commonlib.dto.UserDTO> res = restClient.post()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .body(userIds)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return res;
    }

}
