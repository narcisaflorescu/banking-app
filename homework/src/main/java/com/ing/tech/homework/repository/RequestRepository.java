package com.ing.tech.homework.repository;

import com.ing.tech.homework.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
//    Optional<Request> findRequestById(Long id);

}
