package com.ing.tech.homework.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "request")
public class Request {

    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "from_username")
    private String fromUsername;
    @Column(name = "to_username")
    private String toUsername;
    private String currency;
    private Double amount;
    private String type;
    private String state;
    private LocalDateTime date;

    @JsonIgnore
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    private User user;

    @Column(name = "request_income_id")
    private Long requestIncomeId;


    public Request(String toUsername, String currency, Double amount) {
        this.toUsername = toUsername;
        this.currency = currency;
        this.amount = amount;
    }

    public Request(String fromUsername, String toUsername, String currency, Double amount) {
        this.fromUsername = fromUsername;
        this.toUsername = toUsername;
        this.currency = currency;
        this.amount = amount;
    }



}
