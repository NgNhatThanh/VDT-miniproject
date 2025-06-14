package org.vdt.qlch.speechservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.vdt.commonlib.model.AbstractAuditEntity;

@Entity
@Table(name = "meeting_speeches")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingSpeech extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int meetingId;

    private String content;

    @Column(name = "speaker_id")
    private String speakerId;

    @Column(name = "approved_by")
    private String approvedBy;

    @Enumerated(EnumType.STRING)
    private SpeechStatus status;

    private int duration;

}
