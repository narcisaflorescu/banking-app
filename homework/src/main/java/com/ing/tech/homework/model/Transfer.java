package com.ing.tech.homework.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity(name = "transfer")

public class Transfer {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_username")
    private String senderUsername;

    @Column(name = "receiver_username")
    private String receiverUsername;
    private String currency;
    private Double amount;
    private String type;
    private LocalDateTime date;

    @JsonIgnore
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    private User user;


    public Transfer(String senderUsername, String receiverUsername, String currency, Double amount) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.currency = currency;
        this.amount = amount;
    }


}