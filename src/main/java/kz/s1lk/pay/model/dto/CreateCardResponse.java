package kz.s1lk.pay.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateCardResponse {
    private String id;
    private String object;
    private String brand;
    private long created;
    private String currency;
    private int expMonth;
    private int expYear;
    private String last4;
    private String pin;
    private String status;
    private String type;
}


