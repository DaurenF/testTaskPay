package kz.s1lk.pay.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LoginDto {
    @NotEmpty
    String email;
    @NotEmpty
    String password;
}
