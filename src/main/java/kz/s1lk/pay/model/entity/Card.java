package kz.s1lk.pay.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import kz.s1lk.pay.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@ToString
public class Card extends BaseEntity {

    private String externalId;
    private String object;
    private String brand;
    private long created;
    private String currency;
    private BigDecimal balance;
    private int expMonth;
    private int expYear;
    private String last4;
    private String pin;
    private String status;
    private String type;
    @ManyToOne
    private CardHolder cardHolder;
}
