package org.vdt.qlch.meetingservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.vdt.commonlib.model.AbstractAuditEntity;
import org.vdt.qlch.meetingservice.model.enums.MeetingRoleEnum;

import java.util.List;

@Entity
@Table(name = "meeting_roles")
@Getter
@Setter
public class MeetingRole extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private MeetingRoleEnum name;

    private String description;

    @ManyToMany
    @JoinTable(
            name = "meeting_role_has_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<MeetingPermission> permissions;

}
