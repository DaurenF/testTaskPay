package kz.s1lk.pay.exception;

public class InvalidJwtTokenException extends BaseException {
    public InvalidJwtTokenException(String message, Integer code) {
        super(message, code);
    }
}
