package org.vdt.commonlib.dto;

import java.util.ArrayList;
import java.util.List;

public record ErrorDTO(String errorStatus, String title, String detail, List<String> fieldErrors) {

    public ErrorDTO(String errorStatus, String title, String detail) {
        this(errorStatus, title, detail, new ArrayList<>());
    }

}
