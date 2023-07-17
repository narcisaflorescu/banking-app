package com.ing.tech.homework.service;

import com.ing.tech.homework.dto.AccountDtoGet;
import com.ing.tech.homework.model.Account;
import com.ing.tech.homework.model.User;
import com.ing.tech.homework.repository.AccountRepository;
import com.ing.tech.homework.service.exceptions.AccountNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {
    private AccountRepository accountRepository;
    private ModelMapper modelMapper;

    public List<AccountDtoGet> findAll() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream().map(e -> modelMapper.map(e, AccountDtoGet.class)).collect(Collectors.toList());
    }

    public void deleteAccountById(Long id) {

        Account account = accountRepository.findById(id).orElseThrow(AccountNotFoundException::new);

        User user = account.getUser();
        user.removeAccount(account.getCurrency());
        account.setUser(null);


        accountRepository.deleteById(account.getId());

    }


}
