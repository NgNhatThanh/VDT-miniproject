package org.vdt.qlch.meetingservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.vdt.commonlib.model.AbstractAuditEntity;
import org.vdt.qlch.meetingservice.model.enums.MeetingJoinStatus;

@Entity
@Table(name = "user_join_meeting")
@Getter
@Setter
public class MeetingJoin extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String userId;

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    private MeetingJoinStatus status;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private MeetingRole role;

}
