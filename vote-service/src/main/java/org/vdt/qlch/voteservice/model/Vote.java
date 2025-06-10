package org.vdt.qlch.voteservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "votes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_vote_id")
    private MeetingVote meetingVote;

    private LocalDateTime createdAt;

    private String createdBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "vote_option_selects",
        joinColumns = @JoinColumn(name = "vote_id"),
        inverseJoinColumns = @JoinColumn(name = "option_id"))
    private List<VoteOption> selectedOptions;

}
