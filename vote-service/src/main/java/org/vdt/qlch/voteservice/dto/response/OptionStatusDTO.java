package org.vdt.qlch.voteservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OptionStatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6529685098267757690L;

    private int optionId;

    private int voteCount;

    private List<VoterDTO> voterInfos;

}
