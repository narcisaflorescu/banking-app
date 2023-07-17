package com.ing.tech.homework.repository;

import com.ing.tech.homework.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
