package org.vdt.qlch.meetingservice.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.vdt.commonlib.exception.BadRequestException;
import org.vdt.commonlib.utils.Constants;

public enum MeetingJoinStatus {

    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED");
//    DELEGATED("DELEGATED");

    private final String value;

    MeetingJoinStatus(String value){
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MeetingJoinStatus fromString(String value) {
        for (MeetingJoinStatus type : MeetingJoinStatus.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new BadRequestException(Constants.ErrorCode.INVALID_ENUM_VALUE);
    }
}
