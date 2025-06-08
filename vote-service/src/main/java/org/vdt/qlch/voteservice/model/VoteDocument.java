package org.vdt.qlch.voteservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vote_documents")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_vote_id")
    private MeetingVote vote;

    private int documentId;

}
