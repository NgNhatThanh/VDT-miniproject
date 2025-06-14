package org.vdt.qlch.meetingservice.dto.response;

import org.vdt.qlch.meetingservice.model.MeetingPrivateNote;

import java.time.LocalDateTime;

public record NoteDTO(
        int id,
        String content,
        LocalDateTime updatedAt
) {

    public static NoteDTO from(MeetingPrivateNote note) {
        return new NoteDTO(note.getId(), note.getContent(), note.getUpdatedAt());
    }

}
