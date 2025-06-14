package org.vdt.qlch.meetingservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.vdt.commonlib.model.AbstractAuditEntity;

@Entity
@Table(name = "meeting_join_authorization")
@Getter
@Setter
public class JoinAuthorization extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "join_id")
    private MeetingJoin join;

    private String authorizedId;

}
