package kz.s1lk.pay.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BalanceDto {
    private String object;
    private List<BalanceEntry> available;
    private IssuingBalance issuing;
    private List<BalanceEntry> pending;

    @Getter
    @Setter
    public static class BalanceEntry {
        private int amount;
        private String currency;

    }

    @Getter
    @Setter
    public static class IssuingBalance {
        private List<BalanceEntry> available;

    }


}


