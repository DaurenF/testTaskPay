package kz.s1lk.pay.util;

import kz.s1lk.pay.exception.BaseException;
import kz.s1lk.pay.exception.InvalidJwtTokenException;
import kz.s1lk.pay.exception.MissingJwtTokenException;
import kz.s1lk.pay.model.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

@Component
@Slf4j
public class HttpExceptionHandler {
    public ResponseEntity<ErrorResponse> handleException(Exception e)
    {
        ErrorResponse errorResponse;
        HttpStatus status;
        if (e instanceof MethodArgumentNotValidException)
        {
            errorResponse = new ErrorResponse(e.getClass().getSimpleName(), "Wrong arguments");
            status = HttpStatus.BAD_REQUEST;
        }else
        if (e instanceof MissingRequestHeaderException)
        {
            errorResponse = new ErrorResponse(e.getClass().getSimpleName(), "Unauthorized");
            status = HttpStatus.UNAUTHORIZED;
        }else
        if (e instanceof BaseException || e instanceof IllegalArgumentException)
        {
            BaseException baseException = (BaseException) e;
            errorResponse = new ErrorResponse(baseException.getClass().getSimpleName(), baseException.getMessage());
            status = HttpStatus.valueOf(baseException.getCode().intValue());
        }else {
            Writer buffer = new StringWriter();
            PrintWriter pw = new PrintWriter(buffer);
            e.printStackTrace(pw);
            log.error(e+" \r\n"+buffer);
            errorResponse = new ErrorResponse("InternalServerErrorException", "Internal Server Error");
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        log.info("Client Exception Response Code: "+"0"+status.value());
        log.info("Client Exception Response: "+ ObjectUtils.toString(errorResponse));
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getClass().getSimpleName(), "Access Denied");
        HttpStatus status = HttpStatus.FORBIDDEN;
        logException(ex);
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJwtTokenException(InvalidJwtTokenException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getClass().getSimpleName(), ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        logException(ex);
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(MissingJwtTokenException.class)
    public ResponseEntity<ErrorResponse> handleMissingJwtTokenException(MissingJwtTokenException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getClass().getSimpleName(), ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        logException(ex);
        return new ResponseEntity<>(errorResponse, status);
    }

    private void logException(Exception e) {
        Writer buffer = new StringWriter();
        PrintWriter pw = new PrintWriter(buffer);
        e.printStackTrace(pw);
        log.error(e.toString() + " \r\n" + buffer.toString());
    }
}
