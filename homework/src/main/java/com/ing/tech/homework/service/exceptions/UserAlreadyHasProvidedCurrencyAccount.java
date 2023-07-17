package com.ing.tech.homework.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class UserAlreadyHasProvidedCurrencyAccount extends  RuntimeException{
}
