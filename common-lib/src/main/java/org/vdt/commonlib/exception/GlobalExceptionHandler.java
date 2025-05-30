package org.vdt.commonlib.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.vdt.commonlib.dto.ErrorDTO;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String ERROR_LOG_FORMAT = "Error: URI: {}, ErrorCode: {}, Message: {}";

    private static final String INVALID_REQUEST_INFORMATION_MESSAGE = "Request information is not valid";

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDTO> handleBadRequestException(BadRequestException ex, WebRequest request) {
        return handleBadRequest(ex, request);
    }

    private String getServletPath(WebRequest webRequest) {
        ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
        return servletRequest.getRequest().getServletPath();
    }

    private ResponseEntity<ErrorDTO> handleBadRequest(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 400);
    }

    private ResponseEntity<ErrorDTO> buildErrorResponse(HttpStatus status, String message, List<String> errors,
                                                        Exception ex, WebRequest request, int statusCode) {
        ErrorDTO errorVm =
                new ErrorDTO(status.toString(), status.getReasonPhrase(), message, errors);

        if (request != null) {
            log.error(ERROR_LOG_FORMAT, this.getServletPath(request), statusCode, message);
        }
//        log.error(message, ex);
        return ResponseEntity.status(status).body(errorVm);
    }

}
