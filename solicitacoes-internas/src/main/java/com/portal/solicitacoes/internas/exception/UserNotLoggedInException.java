package com.portal.solicitacoes.internas.exception;

public class UserNotLoggedInException extends RuntimeException{
    public UserNotLoggedInException(String message){
        super(message);
    }
}
