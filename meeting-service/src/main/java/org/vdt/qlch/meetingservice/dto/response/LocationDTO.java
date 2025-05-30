package org.vdt.qlch.meetingservice.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.vdt.qlch.meetingservice.model.MeetingLocation;

public record LocationDTO(
        @NotBlank String name,
        @NotNull String description
) {

    public static LocationDTO from(MeetingLocation location){
        return new LocationDTO(
                location.getName(),
                location.getDescription()
        );
    }

}
