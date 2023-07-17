package com.ing.tech.homework.resource;

import com.ing.tech.homework.dto.*;
import com.ing.tech.homework.model.TransactionInterface;
import com.ing.tech.homework.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.ing.tech.homework.security.AuthorizationRoles.ROLE_ADMIN;
import static com.ing.tech.homework.security.AuthorizationRoles.ROLE_USER;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Secured({ROLE_USER})
public class UserResource {

    private UserService userService;


//    @PostMapping
//    public ResponseEntity<UserDtoPost> save(@RequestBody @Valid UserDtoPost userDto){
//        return ResponseEntity.ok(userService.save(userDto));
//    }

    @PostMapping(path = "/{username}/accounts")
    public ResponseEntity<Void> createAccountInUser(@PathVariable String username, @RequestBody @Valid AccountDtoPost accountDtoPost){
        userService.createAccountInUser(username, accountDtoPost);
        return ResponseEntity.noContent().build();
    }

    @Secured(ROLE_ADMIN)
    @GetMapping
    public ResponseEntity<List<UserDtoGetAll>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDtoGet> getUserByUsername(@PathVariable String username){
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @GetMapping("/{username}/{accountCurrency}")
    public ResponseEntity<Double> getAccountBalance(@PathVariable String username, @PathVariable String accountCurrency){
        return ResponseEntity.ok(userService.getAccountBalance(username,accountCurrency));
    }

    @DeleteMapping("/{id}")
    @Secured(ROLE_ADMIN)
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @Secured({ROLE_ADMIN, ROLE_USER})
    @PostMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationDto> makeInactiveUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok().body(new AuthenticationDto(userService.makeInactiveUserByUsername(username)));
    }

    @DeleteMapping("/{username}/accounts/{currency}")
    public ResponseEntity<Void> deleteUserAccountByCurrency(@PathVariable String username, @PathVariable String currency) {
        userService.deleteUserAccountByCurrency(username, currency);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}/transfers/exchanges")
    public ResponseEntity<List<TransactionInterface>> getUserHistory(@PathVariable String username){
        return ResponseEntity.ok(userService.getUserHistory(username));
    }




    // ------------------------------ TRANSFER MONEY ---------------------------------------------------

    @PostMapping("/{senderUsername}/transfers")
    public ResponseEntity<Void> transferMoneyToAUser(@PathVariable String senderUsername, @RequestBody @Valid TransferDtoPost transferDtoPost){
        userService.transferMoney(senderUsername, transferDtoPost);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}/transfers/type")
    public ResponseEntity<List<TransferDtoGet>> getTransfers(@PathVariable String username, @RequestParam String type){
        return ResponseEntity.ok(userService.getTransfers(username, type));
    }


    // ------------------------------ REQUEST MONEY ---------------------------------------------------

    @PostMapping("/{fromUsername}/requests")
    public ResponseEntity<Void> requestMoney(@PathVariable String fromUsername, @RequestBody @Valid RequestDtoPost requestDtoPost){
        userService.requestMoney(fromUsername, requestDtoPost);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}/requests/type")
    public ResponseEntity<List<RequestDtoGetAccept>> getRequests(@PathVariable String username, @RequestParam String type){
        return ResponseEntity.ok(userService.getRequests(username, type));
    }


    // ------------------------------ ACCEPT/REJECT REQUEST  ---------------------------------------------------

    @PatchMapping("/{username}/requests")
    public ResponseEntity<RequestDtoGet> acceptOrRejectRequest(@PathVariable String username, @RequestParam String type, @RequestParam Long id, @RequestBody @Valid RequestStateWrapper requestStateWrapper){
        return ResponseEntity.ok(userService.acceptOrRejectRequest(username, type, id, requestStateWrapper.getState()));
    }

    // ------------------------------ EXCHANGE MONEY ---------------------------------------------------

    @PostMapping("/{username}/exchanges")
    public ResponseEntity<Void> exchange(@PathVariable String username, @RequestBody @Valid ExchangeDtoPost exchangeDtoPost){
        userService.exchange(username, exchangeDtoPost);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}/exchanges")
    public ResponseEntity<List<ExchangeDtoGet>> getExchanges(@PathVariable String username){
        return ResponseEntity.ok(userService.getExchanges(username));
    }




}
