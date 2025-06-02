package org.vdt.qlch.meetingservice.utils;

public final class Constants {

    public final class ErrorCode{
        public static final String MEETING_LOCATION_NOT_FOUND = "MEETING_LOCATION_NOT_FOUND";
        public static final String STARTTIME_AFTER_ENDTIME_ERROR = "STARTTIME_AFTER_ENDTIME_ERROR";
        public static final String STARTTIME_BEFORE_NOW_ERROR = "STARTTIME_BEFORE_NOW_ERROR";
        public static final String MEETING_LOCATION_EXISTED = "MEETING_LOCATION_EXISTED";
        public static final String PARTICIPANT_NOT_FOUND = "PARTICIPANT_NOT_FOUND";
        public static final String DOCUMENT_NOT_FOUND = "DOCUMENT_NOT_FOUND";
        public static final String MEETING_ROLE_NOT_FOUND = "MEETING_ROLE_NOT_FOUND";
        public static final String STARTDATE_AFTER_ENDDATE_ERROR = "STARTDATE_AFTER_ENDDATE_ERROR";
        public static final String MEETING_NOT_FOUND = "MEETING_NOT_FOUND";
    }

    public final class MeetingRole{
        public static final String GUEST = "GUEST";
        public static final String SECRETARY = "SECRETARY";
        public static final String PARTICIPANT = "PARTICIPANT";
        public static final String DOCUMENT_APPROVER = "DOCUMENT_APPROVER";
        public static final String HOST = "HOST";

    }

}
