package org.vdt.qlch.userservice.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.vdt.commonlib.dto.RecordExistDTO;
import org.vdt.commonlib.exception.NotFoundException;
import org.vdt.qlch.userservice.config.KeycloakPropsConfig;
import org.vdt.qlch.userservice.dto.UserDTO;
import org.vdt.qlch.userservice.utils.Constants;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Keycloak keycloak;

    private final KeycloakPropsConfig keycloakPropsConfig;

    public RecordExistDTO checkExistsById(List<String> userIds){
        boolean exists = new HashSet<>(keycloak.realm(keycloakPropsConfig.getRealm()).users().list()
                .stream().map(UserRepresentation::getId)
                .toList())
                .containsAll(userIds);
        return new RecordExistDTO(exists);
    }

    public List<UserDTO> getAll() {
        List<UserRepresentation> allUsers = keycloak.realm(keycloakPropsConfig.getRealm()).users().list();
        return allUsers.stream().map(UserDTO::from).toList();
    }

    public UserDTO getById(String userId) {
        try{
            UserRepresentation userRepresentation = keycloak.realm(keycloakPropsConfig.getRealm())
                    .users().get(userId).toRepresentation();
            return UserDTO.from(userRepresentation);
        }
        catch (jakarta.ws.rs.NotFoundException e){
            throw new NotFoundException(Constants.ErrorCode.USER_NOT_FOUND);
        }
    }
}
