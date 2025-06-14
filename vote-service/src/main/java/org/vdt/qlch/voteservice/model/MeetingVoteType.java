package org.vdt.qlch.voteservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.vdt.commonlib.exception.BadRequestException;
import org.vdt.commonlib.utils.Constants;

public enum MeetingVoteType {
    PUBLIC("PUBLIC"),
    PRIVATE("PRIVATE");

    private String value;

    MeetingVoteType(String value){
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MeetingVoteType fromString(String value) {
        for (MeetingVoteType type : MeetingVoteType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new BadRequestException(Constants.ErrorCode.INVALID_ENUM_VALUE);
    }

}
