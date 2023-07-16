package kz.s1lk.pay.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CardHolderResponse {
    private String id;
    private String object;
    private String company;
    private long created;
    private String email;
    private String name;
    private String phoneNumber;
    private String status;
    private String type;

}
