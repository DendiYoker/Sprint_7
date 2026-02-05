package ru.qascooter.dto.requestbody;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCourier {
    private String login;
    private String password;
    private String firstName;

}
