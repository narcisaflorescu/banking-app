package com.ing.tech.homework.dto;

import lombok.Data;

import javax.validation.constraints.Positive;

@Data
public class AccountAmountWrapper {
    @Positive
    private Double amount;
}
