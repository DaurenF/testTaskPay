package kz.s1lk.pay.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import kz.s1lk.pay.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardHolder extends BaseEntity {

    private String externalId;
    private String name;
    private String email;
    private String phone_number;
    private String status;
    private String type;
    private String first_name;
    private String last_name;
    private String city;
    private String state;
    private String postal_code;
    private String country;
    private String password;
    @OneToMany
    @JoinTable(
            name = "cards",
            joinColumns = @JoinColumn(name = "card_holder_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private List<Card> cards;
}
