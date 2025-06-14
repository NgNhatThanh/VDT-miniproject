package org.vdt.qlch.voteservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OptionVoteCount {

    private int optionId;

    private int voteCount;

}
