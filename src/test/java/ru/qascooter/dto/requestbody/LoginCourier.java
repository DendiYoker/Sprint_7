package ru.qascooter.dto.requestbody;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginCourier {
    private String login;
    private String password;
}
