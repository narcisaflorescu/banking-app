package com.ing.tech.homework.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountDtoPost {
    @Positive
    private Double balance;
    @NotBlank
    private String currency;
}
