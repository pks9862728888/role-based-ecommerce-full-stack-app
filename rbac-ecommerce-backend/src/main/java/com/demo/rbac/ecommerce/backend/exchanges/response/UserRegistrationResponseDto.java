package com.demo.rbac.ecommerce.backend.exchanges.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserRegistrationResponseDto {
    private boolean status;
    private String message;
}
