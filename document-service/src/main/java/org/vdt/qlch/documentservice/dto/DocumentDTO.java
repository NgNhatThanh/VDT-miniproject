package org.vdt.qlch.documentservice.dto;

import org.vdt.qlch.documentservice.model.Document;

public record DocumentDTO(
        String name,
        String url,
        int size
) {

    public static DocumentDTO from(Document document) {
        return new DocumentDTO(
                document.getName(),
                document.getUrl(),
                document.getSize()
        );
    }
}
