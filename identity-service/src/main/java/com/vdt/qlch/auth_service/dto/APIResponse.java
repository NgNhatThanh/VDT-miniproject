package com.vdt.qlch.auth_service.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class APIResponse<T> {

    private int code;

    private String message;

    private String error;

    private T data;

}
