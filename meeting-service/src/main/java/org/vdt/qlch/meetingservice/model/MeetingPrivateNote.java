package org.vdt.qlch.meetingservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.vdt.commonlib.model.AbstractAuditEntity;

@Entity
@Table(name = "meeting_private_notes")
@Getter
@Setter
public class MeetingPrivateNote extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

}
