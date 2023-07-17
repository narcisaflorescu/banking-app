package com.ing.tech.homework.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class RequestDtoPost {

    @NotBlank
    private String toUsername;
    @NotBlank
    private String currency;
    @Positive
    private Double amount;




}
