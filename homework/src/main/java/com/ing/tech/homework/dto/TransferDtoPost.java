package com.ing.tech.homework.dto;

import com.ing.tech.homework.model.TransactionInterface;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class TransferDtoPost implements TransactionInterface {

    private String senderUsername;
    @NotBlank
    private String receiverUsername;
    @NotBlank
    private String currency;
    @Positive
    private Double amount;
    private String type;

    public TransferDtoPost(String receiverUsername, String currency, Double amount) {
        this.receiverUsername = receiverUsername;
        this.currency = currency;
        this.amount = amount;
    }
}
