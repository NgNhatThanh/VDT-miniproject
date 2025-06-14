package org.vdt.qlch.meetingservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.vdt.commonlib.model.AbstractAuditEntity;

import java.util.List;

@Entity
@Table(name = "meeting_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRole extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String description;

    @ManyToMany
    @JoinTable(
            name = "meeting_role_has_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<MeetingPermission> permissions;

}
