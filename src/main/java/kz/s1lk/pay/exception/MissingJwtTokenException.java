package kz.s1lk.pay.exception;

public class MissingJwtTokenException extends BaseException {
    public MissingJwtTokenException(String message, Integer code) {
        super(message, code);
    }
}
