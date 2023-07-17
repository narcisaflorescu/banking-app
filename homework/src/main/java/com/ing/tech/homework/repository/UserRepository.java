package com.ing.tech.homework.repository;

import com.ing.tech.homework.model.Account;
import com.ing.tech.homework.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    //Optional<User> findUserByAccount(Account account);
}
