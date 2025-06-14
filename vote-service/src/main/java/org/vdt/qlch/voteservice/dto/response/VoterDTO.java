package org.vdt.qlch.voteservice.dto.response;

import org.vdt.commonlib.dto.UserDTO;

import java.io.Serializable;

public record VoterDTO(
        String fullName,
        String picture
) implements Serializable {

    public static VoterDTO from(UserDTO u){
        return new VoterDTO(
                u.fullName(),
                u.picture()
        );
    }

}
