package kz.s1lk.pay.exception;

public class IncorrectCredentialsException extends BaseException {
    public IncorrectCredentialsException(String message, Integer code) {
        super(message, code);
    }
}
