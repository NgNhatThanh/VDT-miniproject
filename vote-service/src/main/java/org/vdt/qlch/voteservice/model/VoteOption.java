package org.vdt.qlch.voteservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vote_question_options")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private VoteQuestion question;

}
