package org.vdt.qlch.meetingservice.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.vdt.commonlib.exception.BadRequestException;

public enum MeetingJoinStatus {

    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected"),
    AUTHORIZED("authorized");

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
        throw new BadRequestException("Invalid status: " + value);
    }
}
