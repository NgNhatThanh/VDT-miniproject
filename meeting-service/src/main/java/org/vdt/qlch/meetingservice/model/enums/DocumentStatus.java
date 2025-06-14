package org.vdt.qlch.meetingservice.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.vdt.commonlib.exception.BadRequestException;
import org.vdt.commonlib.utils.Constants;

public enum DocumentStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String value;

    DocumentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DocumentStatus fromString(String value) {
        for (DocumentStatus type : DocumentStatus.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new BadRequestException(Constants.ErrorCode.INVALID_ENUM_VALUE);
    }

}
