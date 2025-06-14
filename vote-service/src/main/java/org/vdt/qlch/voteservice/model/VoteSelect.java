package org.vdt.qlch.voteservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vote_option_selects")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoteSelect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "vote_id")
    private Integer voteId;

    @Column(name = "option_id")
    private Integer optionId;

    public VoteSelect(Integer voteId, Integer optionId) {
        this.voteId = voteId;
        this.optionId = optionId;
    }

}
