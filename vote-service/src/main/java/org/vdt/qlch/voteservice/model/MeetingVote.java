package org.vdt.qlch.voteservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.vdt.commonlib.model.AbstractAuditEntity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "meeting_votes")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingVote extends AbstractAuditEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int meetingId;

    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private MeetingVoteType type;

    @OneToMany(mappedBy = "vote",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private List<VoteDocument> documents;

    @OneToMany(mappedBy = "vote",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private List<VoteQuestion> questions;

}
