package com.ing.tech.homework.dto;

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

public class RequestDtoGetAccept {


    private Long id;
    private String fromUsername;
    private String toUsername;
    private String currency;
    private Double amount;
    private String type;
    private String state;
    private LocalDateTime date;


}
