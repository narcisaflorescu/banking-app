package com.ing.tech.homework.repository;

import com.ing.tech.homework.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findAccountByCurrency(String accountCurrency);
}
