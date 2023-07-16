package kz.s1lk.pay.model.entity;

import jakarta.persistence.Entity;
import kz.s1lk.pay.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Transfer extends BaseEntity {
    private String externalId;
    private String object;
    private int amount;
    private String balance_transaction;
    private long created;
    private String currency;
    private String description;
    private String destination;
    private String destination_payment;
    private String source_type;
    private String transfer_group;

}
