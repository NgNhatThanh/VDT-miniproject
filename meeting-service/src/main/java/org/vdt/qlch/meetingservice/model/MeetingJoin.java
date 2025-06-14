package org.vdt.qlch.meetingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.vdt.commonlib.model.AbstractAuditEntity;
import org.vdt.qlch.meetingservice.model.enums.MeetingJoinStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_join_meeting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MeetingJoin extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String userId;

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @Enumerated(EnumType.STRING)
    private MeetingJoinStatus status;

    @ManyToMany
    @JoinTable(name = "meeting_join_roles",
        joinColumns = @JoinColumn(name = "join_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<MeetingRole> roles = new ArrayList<>();

    private String rejectReason;

}
