package com.portal.solicitacoes.internas.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ErrorResponse response = new ErrorResponse(
                400,
                "Erro de validação",
                errors
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex) {

        Map<String, String> errors = new HashMap<>();

        errors.put("email", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                409,
                "conflict",
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(UserNotLoggedInException.class)
    public ResponseEntity<ErrorResponse> handleUserNotLoggedIn(UserNotLoggedInException ex) {

        Map<String, String> errors = new HashMap<>();

        errors.put("email", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                401,
                "O usuario não esta logado",
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex){
        Map<String, String> erros = new HashMap<>();
        erros.put("email", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                404,
                "not found",
                erros
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(NotFoundRequestException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundRequestException(NotFoundRequestException ex){
        Map<String, String> erros = new HashMap<>();
        erros.put("email", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                404,
                "not found",
                erros
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        Map<String, String> errors = new HashMap<>();

        errors.put("credentials", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                401,
                "Não autorizado",
                errors
        );

        return ResponseEntity
                .status(401)
                .body(response);
    }
}
