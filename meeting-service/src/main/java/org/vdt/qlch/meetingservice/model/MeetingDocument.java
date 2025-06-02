package org.vdt.qlch.meetingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.vdt.commonlib.model.AbstractAuditEntity;
import org.vdt.qlch.meetingservice.model.enums.DocumentStatus;

@Entity
@Table(name = "meeting_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MeetingDocument extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    private int documentId;

    private DocumentStatus status;

    private String approvedBy;

}