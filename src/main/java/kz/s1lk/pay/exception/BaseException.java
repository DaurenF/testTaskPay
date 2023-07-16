package kz.s1lk.pay.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BaseException extends  RuntimeException{
    private String message;
    private Integer code;

}
