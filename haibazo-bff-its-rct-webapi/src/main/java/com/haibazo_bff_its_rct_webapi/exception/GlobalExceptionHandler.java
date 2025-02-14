package com.haibazo_bff_its_rct_webapi.exception;

import com.haibazo.bff.common.extension.exception.BadRequestException;
import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.ErrorDetail;
import com.haibazo_bff_its_rct_webapi.dto.ErrorResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCategoryResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctStyleResponse;
import com.haibazo_bff_its_rct_webapi.service.CategoryService;
import com.haibazo_bff_its_rct_webapi.service.StyleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Properties messages;
    private final String host = "ecommerce-service";
    private final CategoryService categoryService;
    private final StyleService styleService;


    public void loadMessages(InputStream input) throws IOException {
        messages.load(input);
    }

    public GlobalExceptionHandler(CategoryService categoryService, StyleService styleService) throws IOException {
        this.categoryService = categoryService;
        this.styleService = styleService;
        messages = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("messages_vn_VN.properties")) {
            if (input != null) {
                messages.load(input);
            } else {
                throw new IOException("File not found: messages_vn_VN.properties");
            }
        }
    }

    @ExceptionHandler(ErrorReviewProductException.class)
    public ResponseEntity<ErrorResponse> handleErrorReviewProductException(HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        List<ErrorDetail> errors = new ArrayList<>();

        String errorKey = "ERRORREVIEWPRODUCT";
        String errorMessage = messages.getProperty(errorKey);

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorCode("400");
        errorDetail.setErrorMessageId(errorKey);
        errorDetail.setErrorMessage(errorMessage);

        errors.add(errorDetail);
        errorResponse.setErrors(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ErrorDeleteUserException.class)
    public ResponseEntity<ErrorResponse> handleErrorDeleteUserException(HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.FORBIDDEN.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        List<ErrorDetail> errors = new ArrayList<>();

        String errorKey = "ERRORDELETEUSER";
        String errorMessage = messages.getProperty(errorKey);

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorCode("403");
        errorDetail.setErrorMessageId(errorKey);
        errorDetail.setErrorMessage(errorMessage);

        errors.add(errorDetail);
        errorResponse.setErrors(errors);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        List<ErrorDetail> errors = new ArrayList<>();

        String errorKey = "UNAUTHORIZED401E";
        String errorMessage = messages.getProperty(errorKey);

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorCode("401");
        errorDetail.setErrorMessageId(errorKey);
        errorDetail.setErrorMessage(errorMessage);

        errors.add(errorDetail);
        errorResponse.setErrors(errors);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(ErrorPermissionException.class)
    public ResponseEntity<ErrorResponse> handleErrorPermissionException(HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.FORBIDDEN.value());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        List<ErrorDetail> errors = new ArrayList<>();

        String errorKey = "ERRORPERMISSION";
        String errorMessage = messages.getProperty(errorKey);

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorCode("403");
        errorDetail.setErrorMessageId(errorKey);
        errorDetail.setErrorMessage(errorMessage);

        errors.add(errorDetail);
        errorResponse.setErrors(errors);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
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
    public String handleListProductEmptyException(Model model) {
        String errorKey = "LISTPRODUCTISEMPTY";
        String errorMessage = messages.getProperty(errorKey).replace("{0}", host);

        // Lấy danh sách danh mục và kiểu
        APICustomize<List<ItsRctCategoryResponse>> categoryResponse = categoryService.categories();
        APICustomize<List<ItsRctStyleResponse>> stylesResponse = styleService.styles();
        model.addAttribute("categories", categoryResponse.getResult());
        model.addAttribute("styles", stylesResponse.getResult());
        model.addAttribute("errorMessage", errorMessage);
        
        return "products";
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