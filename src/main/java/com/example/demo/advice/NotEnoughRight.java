package com.example.demo.advice;
import com.example.demo.exception.NotEnoughRightException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NotEnoughRight {

    @ResponseBody
    @ExceptionHandler(NotEnoughRightException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String NotEnoughRightHandler (NotEnoughRightException ex) {
        return ex.getMessage();
    }
}
