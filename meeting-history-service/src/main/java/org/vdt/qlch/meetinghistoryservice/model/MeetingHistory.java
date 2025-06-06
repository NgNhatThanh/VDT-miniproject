package org.vdt.qlch.meetinghistoryservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.vdt.commonlib.model.AbstractAuditEntity;
import org.vdt.commonlib.model.MeetingHistoryType;

@Entity
@Table(name = "meeting_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MeetingHistory extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int meetingId;

    private String content;

    @Enumerated(EnumType.STRING)
    private MeetingHistoryType type;

}