package com.vdt.qlch.auth_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenVerificationResponse {

    private boolean valid;

}
