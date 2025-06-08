package org.vdt.qlch.voteservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.vdt.commonlib.dto.RecordExistDTO;
import org.vdt.qlch.voteservice.config.ServiceUrlConfig;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final RestClient restClient;

    private final ServiceUrlConfig serviceUrlConfig;

    public boolean checkExistById(List<Integer> documentIds){
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromUriString(serviceUrlConfig.document())
                .path("/exist")
                .buildAndExpand()
                .toUri();
        RecordExistDTO res = restClient.post()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .body(documentIds)
                .retrieve()
                .body(RecordExistDTO.class);
        return res != null && res.exist();
    }

}
