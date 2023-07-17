package com.ing.tech.homework.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExchangeDtoPost {
    @NotBlank
    private String fromCurrency;
    @NotBlank
    private String toCurrency;
    @Positive
    private Double amount;

}
