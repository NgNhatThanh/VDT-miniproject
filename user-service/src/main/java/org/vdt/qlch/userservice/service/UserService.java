package org.vdt.qlch.userservice.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.vdt.qlch.userservice.config.KeycloakPropsConfig;
import org.vdt.qlch.userservice.dto.UserExistByIdDTO;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Keycloak keycloak;

    private final KeycloakPropsConfig keycloakPropsConfig;

    public UserExistByIdDTO checkExistsById(List<String> userIds){
        boolean exists = new HashSet<>(keycloak.realm(keycloakPropsConfig.getRealm()).users().list()
                .stream().map(UserRepresentation::getId)
                .toList())
                .containsAll(userIds);
        return new UserExistByIdDTO(exists);
    }

}
