package com.ing.tech.homework.repository;

import com.ing.tech.homework.model.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
}
