package com.ing.tech.homework.dto;

import com.ing.tech.homework.model.TransactionInterface;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class TransferDtoGet implements TransactionInterface {

    private String senderUsername;
    private String receiverUsername;
    private String currency;
    private Double amount;
    private String type;
    private LocalDateTime date;


}
