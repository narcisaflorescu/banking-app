package com.ing.tech.homework.resource;

import com.ing.tech.homework.dto.AccountDtoGet;
import com.ing.tech.homework.dto.AccountDtoPost;
import com.ing.tech.homework.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ing.tech.homework.security.AuthorizationRoles.ROLE_ADMIN;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
@Secured({ROLE_ADMIN})

public class AccountResource {

    private AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountDtoGet>> getAllRequests(){
        return ResponseEntity.ok(accountService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccountById(@PathVariable Long id) {
        accountService.deleteAccountById(id);
        return ResponseEntity.noContent().build();
    }
}
