package com.bank.webApplication.ExceptionHandler;

import com.bank.webApplication.CustomException.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler{

    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }
   //Error-401
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Object> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    //Error-404
    @ExceptionHandler({UserNotFoundException.class, AccountNotFoundException.class,LogNotFoundException.class})
    public ResponseEntity<Object> handleNotFound(RuntimeException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

   //Error-409
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Object> handleInsufficientFunds(InsufficientFundsException ex) {
        return buildResponse(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, ex.getMessage());
    }
   //Error-400
    @ExceptionHandler(InvalidAccountTypeException.class)
    public ResponseEntity<Object> handleInvalidAccountType(InvalidAccountTypeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    //Error-409
    @ExceptionHandler(DuplicateAccountException.class)
    public ResponseEntity<Object> handleDuplicateAccount(DuplicateAccountException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    //Error-422
    @ExceptionHandler(TransactionFailedException.class)
    public ResponseEntity<Object> handleTransactionFailed(TransactionFailedException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

   //Error-500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }
    //Error-409
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<Object> handleUserAlreadyExistException(Exception ex){
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    //Error-403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex){
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }
    @ExceptionHandler(RefreshTokenExpired.class)
    public ResponseEntity<Object> handleRefreshTokenExpired(Exception ex){
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }
}
