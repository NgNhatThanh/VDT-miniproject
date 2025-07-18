package org.vdt.qlch.voteservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "vote_questions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteQuestion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_vote_id")
    private MeetingVote vote;

    @OneToMany(mappedBy = "question",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private List<VoteOption> options;

}
