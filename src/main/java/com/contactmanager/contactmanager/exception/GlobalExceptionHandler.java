package com.contactmanager.contactmanager.exception;

import com.contactmanager.contactmanager.payload.responseDto.ApiResponse;
import com.contactmanager.contactmanager.payload.responseDto.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ApiError apiError = new ApiError("User not found with username: "+ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiError, apiError.getStatusCode());
    }

//    @ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<ApiError> handleAuthenticationException(BadCredentialsException ex) {
//        ApiError apiError = new ApiError("Authentication failed: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
//        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
//    }

    @ExceptionHandler(UserExistException.class)
    public ResponseEntity<ApiError> handleUserExistException(UserExistException ex) {
        ApiError apiError = new ApiError("Authentication failed: " + ex.getMessage(), HttpStatus.CONFLICT);
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleAuthenticationException(BadCredentialsException ex) {
        ApiResponse apiResponse = ResponseUtil.error(401,"User Not Authenticated!",ex.getMessage(), null,null);
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Result handleValidationException(MethodArgumentNotValidException ex) {

        List<ObjectError> errors = ex.getBindingResult().getAllErrors();

        Map<String, String> map = new HashMap<>(errors.size());
        errors.forEach((error) -> {
            String key = ((FieldError) error).getField();
            String val = error.getDefaultMessage();
            map.put(key, val);
        });
        return new Result(false, StatusCode.BAD_REQUEST, "Provided arguments are invalid, see data for details.", map);
    }


    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ResponseEntity<ApiResponse> handleAuthorizationException(UnAuthorizedException ex) {
        ApiResponse apiResponse =  ResponseUtil.error(HttpStatus.UNAUTHORIZED.value(), "Bearer Token is Missing.",ex.getMessage(),null,null);
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<ApiResponse> handleAuthorizationException(ResourceNotFoundException ex) {
        ApiResponse apiResponse =  ResponseUtil.error(HttpStatus.NOT_FOUND.value(), "Resource not found",ex.getMessage(),null,null);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        ApiError apiError = new ApiError("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
