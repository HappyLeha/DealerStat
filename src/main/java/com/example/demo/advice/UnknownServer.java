package com.example.demo.advice;
import com.example.demo.exception.UnknownServerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class UnknownServer {

    @ResponseBody
    @ExceptionHandler(UnknownServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String UnknownServerHandler (UnknownServerException ex) {
        return ex.getMessage();
    }
}
