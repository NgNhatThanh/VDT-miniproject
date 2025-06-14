package org.vdt.qlch.meetingservice.dto.response;

import org.vdt.commonlib.dto.DocumentDTO;

public record DocumentDTOForApprovement(
        int meetingDocumentId,
        DocumentDTO detail,
        String uploaderFullName
) {
}
