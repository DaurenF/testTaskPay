package kz.s1lk.pay.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class FundBalanceRequest {
    private BigDecimal amount;
    private String currency;
}
