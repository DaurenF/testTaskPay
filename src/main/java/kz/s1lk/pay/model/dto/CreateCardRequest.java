package kz.s1lk.pay.model.dto;

import kz.s1lk.pay.model.enums.CardType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateCardRequest {
    private String cardholder;
    private String currency;
    private CardType type;
}
