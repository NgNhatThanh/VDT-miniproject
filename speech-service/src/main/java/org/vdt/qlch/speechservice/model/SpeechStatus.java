package org.vdt.qlch.speechservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.vdt.commonlib.exception.BadRequestException;
import org.vdt.commonlib.utils.Constants;

public enum SpeechStatus {
    PENDING("PENDING"),
    REJECTED("REJECTED"),
    APPROVED("APPROVED"),
    ON_GOING("ON_GOING"),
    ENDED("ENDED");

    private final String value;

    SpeechStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SpeechStatus fromString(String value) {
        for (SpeechStatus type : SpeechStatus.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new BadRequestException(Constants.ErrorCode.INVALID_ENUM_VALUE);
    }
}
