package com.haibazo_bff_its_rct_webapi.exception;

import com.haibazo.bff.common.extension.exception.BadRequestException;
import com.haibazo_bff_its_rct_webapi.dto.ErrorDetail;
import com.haibazo_bff_its_rct_webapi.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Properties messages;
    private final String host = "ecommerce-service";

    public void loadMessages(InputStream input) throws IOException {
        messages.load(input);
    }

    public GlobalExceptionHandler() throws IOException {
        messages = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("messages_vn_VN.properties")) {
            if (input != null) {
                messages.load(input);
            } else {
                throw new IOException("File not found: messages_vn_VN.properties");
            }
        }
    }

    @ExceptionHandler(MinSpendCouponException.class)
    public ResponseEntity<ErrorResponse> handleMinSpendCouponException(HttpServletRequest request, MinSpendCouponException ex) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        List<ErrorDetail> errors = new ArrayList<>();

        String errorKey = "MINSPENDCOUPON";
        String errorMessage = messages.getProperty(errorKey)
                .replace("{0}", ex.getMinSpend().toString());

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorCode("404");
        errorDetail.setErrorMessageId(errorKey);
        errorDetail.setErrorMessage(errorMessage);

        errors.add(errorDetail);
        errorResponse.setErrors(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CouponExpiredException.class)
    public ResponseEntity<ErrorResponse> handleCouponExpiredException(HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        List<ErrorDetail> errors = new ArrayList<>();

        String errorKey = "COUPONEXPIRED";
        String errorMessage = messages.getProperty(errorKey);

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorCode("404");
        errorDetail.setErrorMessageId(errorKey);
        errorDetail.setErrorMessage(errorMessage);

        errors.add(errorDetail);
        errorResponse.setErrors(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CouponAlreadyUsedException.class)
    public ResponseEntity<ErrorResponse> handleCouponAlreadyUsedException(HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        List<ErrorDetail> errors = new ArrayList<>();

        String errorKey = "COUPOMALREADYUSED";
        String errorMessage = messages.getProperty(errorKey);

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorCode("404");
        errorDetail.setErrorMessageId(errorKey);
        errorDetail.setErrorMessage(errorMessage);

        errors.add(errorDetail);
        errorResponse.setErrors(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CartEmptyException.class)
    public ResponseEntity<ErrorResponse> handleCartEmptyException(HttpServletRequest request, CartEmptyException ex) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        List<ErrorDetail> errors = new ArrayList<>();

        String errorKey = "CARTISEMPTY";
        String errorMessage = messages.getProperty(errorKey)
                .replace("{0}", ex.getUserId().toString());

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorCode("404");
        errorDetail.setErrorMessageId(errorKey);
        errorDetail.setErrorMessage(errorMessage);

        errors.add(errorDetail);
        errorResponse.setErrors(errors);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(HttpServletRequest request, InsufficientStockException ex) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        List<ErrorDetail> errors = new ArrayList<>();

        String errorKey = "INSUFFICIENTSTOCK";
        String errorMessage = messages.getProperty(errorKey)
                .replace("{0}", ex.getProductName());

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorCode("404");
        errorDetail.setErrorMessageId(errorKey);
        errorDetail.setErrorMessage(errorMessage);

        errors.add(errorDetail);
        errorResponse.setErrors(errors);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
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

    @ExceptionHandler(InvalidPageOrSizeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPageOrSizeException(HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        List<ErrorDetail> errors = new ArrayList<>();

        String errorKey = "INVALIDPAGEORSIZE";
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorCode("400");
        errorDetail.setErrorMessageId(errorKey);
        String errorMessage = messages.getProperty(errorKey).replace("{0}", host);
        errorDetail.setErrorMessage(errorMessage);

        errors.add(errorDetail);
        errorResponse.setErrors(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ListProductEmptyException.class)
    public ResponseEntity<ErrorResponse> handleListProductEmptyException(HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        List<ErrorDetail> errors = new ArrayList<>();

        String errorKey = "LISTPRODUCTISEMPTY";
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorCode("404");
        errorDetail.setErrorMessageId(errorKey);
        String errorMessage = messages.getProperty(errorKey).replace("{0}", host);
        errorDetail.setErrorMessage(errorMessage);

        errors.add(errorDetail);
        errorResponse.setErrors(errors);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        List<ErrorDetail> errors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorKey = "validation.annotation.NoSpecialCharacters.message";
            String fieldName = ((FieldError) error).getField();
            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.setErrorCode("400");
            errorDetail.setErrorMessageId(errorKey);
            String errorMessage = messages.getProperty(errorKey).replace("{0}", host);
            errorDetail.setErrorMessage(String.format("%s: %s", fieldName, errorMessage));
            errors.add(errorDetail);
        });

        errorResponse.setErrors(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        List<ErrorDetail> errors = new ArrayList<>();

        String errorKey = "BADREQUEST4001E";
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorCode("400");
        errorDetail.setErrorMessageId(errorKey);
        String errorMessage = messages.getProperty(errorKey).replace("{0}", host);
        errorDetail.setErrorMessage(errorMessage);

        errors.add(errorDetail);
        errorResponse.setErrors(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

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

}