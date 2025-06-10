package org.vdt.qlch.voteservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.vdt.commonlib.dto.DocumentDTO;
import org.vdt.commonlib.dto.RecordExistDTO;
import org.vdt.qlch.voteservice.config.ServiceUrlConfig;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public List<DocumentDTO> getList(List<Integer> documentIds){
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromUriString(serviceUrlConfig.document())
                .path("/get-list-documents")
                .buildAndExpand()
                .toUri();
        List<DocumentDTO> res = new ArrayList<>();
        try{
            res = restClient.post()
                    .uri(url)
                    .headers(h -> h.setBearerAuth(jwt))
                    .body(documentIds)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
        return res;
    }

}
