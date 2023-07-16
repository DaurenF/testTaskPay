package kz.s1lk.pay.exception;

public class CardHolderAlreadyExists extends BaseException{
    public CardHolderAlreadyExists(String message, Integer code) {
        super(message, code);
    }
}
