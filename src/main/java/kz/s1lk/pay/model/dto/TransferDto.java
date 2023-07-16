package kz.s1lk.pay.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferDto {
    private String object;
    private BigDecimal amount;
    private String balance_transaction;
    private long created;
    private String currency;
    private String description;
    private String fromCard;
    private String toCard;
    private String source_type;
    private String transfer_group;
}
