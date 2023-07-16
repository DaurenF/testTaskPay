package kz.s1lk.pay.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class CardHolderRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    //todo regex
    private String phone_number;

    @NotBlank(message = "Status is required")
    private String status;

    @NotBlank(message = "Type is required")
    private String type;

    private IndividualDTO individual;
    private BillingDTO billing;

    @NotBlank(message = "Password is required")
    private String password;

    @ToString
    @Getter
    @Setter
    public static class IndividualDTO {
        @NotBlank(message = "First name is required")
        private String first_name;
        @NotBlank(message = "Last name is required")
        private String last_name;
        private DateOfBirthDTO dob;

        @ToString
        @Getter
        @Setter
        public static class DateOfBirthDTO {
            private int day;
            private int month;
            private int year;
        }
    }

    @ToString
    @Getter
    @Setter
    public static class BillingDTO {
        private AddressDTO address;

        @ToString
        @Getter
        @Setter
        public static class AddressDTO {
            private String line1;
            private String city;
            private String state;
            private String postal_code;
            private String country;

        }
    }
}
