package com.ing.tech.homework.model;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;


public interface TransactionInterface {


     LocalDateTime date = null;


    default LocalDateTime getDate() {
        return date;
    }
}
