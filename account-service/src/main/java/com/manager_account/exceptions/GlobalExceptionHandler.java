package com.manager_account.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.manager_account.dto.response.ErrorDetail;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.manager_account.dto.response.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Properties messages;
    private final String host = "account-service";

    public GlobalExceptionHandler() throws IOException {
        messages = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("messages_en_US.properties")) {
            if (input != null) {
                messages.load(input);
            } else {
                throw new IOException("File not found: messages_en_US.properties");
            }
        }
    }

    //Xu ly chung
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setDebugMessage("An unexpected error occurred chung.");

        List<ErrorDetail> errors = new ArrayList<>();
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorCode("INTERNAL_SERVER_ERROR");
        errorDetail.setErrorMessageId("UNKNOWN_ERROR");
        errorDetail.setErrorMessage(ex.getMessage());

        errors.add(errorDetail);
        errorResponse.setErrors(errors);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsException(HttpServletRequest request, ResourceAlreadyExistsException ex) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.CONFLICT.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        List<ErrorDetail> errors = new ArrayList<>();

        String errorKey = "RESOURCEALREADYEXISTS";
        String errorMessage = messages.getProperty(errorKey)
                .replace("{0}", ex.getResourceName())
                .replace("{1}", ex.getFieldName())
                .replace("{2}", ex.getFieldValue());

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorCode("409");
        errorDetail.setErrorMessageId(errorKey);
        errorDetail.setErrorMessage(errorMessage);

        errors.add(errorDetail);
        errorResponse.setErrors(errors);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(HttpServletRequest request, ResourceNotFoundException ex) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        List<ErrorDetail> errors = new ArrayList<>();

        String errorKey = "NOTFOUND4041E";
        String errorMessage = messages.getProperty(errorKey)
                .replace("{0}", ex.getResourceName())
                .replace("{1}", ex.getFieldName())
                .replace("{2}", ex.getFieldValue());

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorCode("404");
        errorDetail.setErrorMessageId(errorKey);
        errorDetail.setErrorMessage(errorMessage);

        errors.add(errorDetail);
        errorResponse.setErrors(errors);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ErrorSignInException.class)
    public ResponseEntity<ErrorResponse> handleErrorSignInException(HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        List<ErrorDetail> errors = new ArrayList<>();

        String errorKey = "ERRORSIGNIN";
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorCode("400");
        errorDetail.setErrorMessageId(errorKey);
        String errorMessage = messages.getProperty(errorKey).replace("{0}", host);
        errorDetail.setErrorMessage(errorMessage);

        errors.add(errorDetail);
        errorResponse.setErrors(errors);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }




//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
//        // Xử lý khi truyền lỗi các tham số
//        ErrorResponse errorResponse = new ErrorResponse(
//                "Invalid parameter: " + ex.getName(), 
//                "Error Code: 400"
//        );
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//    }
    
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//
//        ex.getBindingResult().getAllErrors().forEach((error) -> {
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName, errorMessage);
//        });
//
//        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
//    }
    
}
