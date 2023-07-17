package com.ing.tech.homework.dto;

import com.ing.tech.homework.model.TransactionInterface;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExchangeDtoGet implements TransactionInterface {

    private String fromCurrency;
    private String toCurrency;
    private Double amount;
    private LocalDateTime date;
}
