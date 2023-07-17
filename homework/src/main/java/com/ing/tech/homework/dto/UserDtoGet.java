package com.ing.tech.homework.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDtoGet {

    private String username;

    private List<AccountDtoPost> accounts;
    private List<TransferDtoGet> transfers;
    private List<RequestDtoGet> requests;
    private List<ExchangeDtoGet> exchanges;

    private String authorizationRoles;
    private String state;
}
