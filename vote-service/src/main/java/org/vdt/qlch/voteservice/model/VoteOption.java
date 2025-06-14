package org.vdt.qlch.voteservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "vote_question_options")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteOption implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private VoteQuestion question;

}
