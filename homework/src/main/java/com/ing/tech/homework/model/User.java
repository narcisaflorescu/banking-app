package com.ing.tech.homework.model;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;

import static com.ing.tech.homework.model.UserState.ACTIVE;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "user")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "user_name", unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "authorization_roles")
    private String authorizationRoles;

    @Column(nullable = false)
    private String state = ACTIVE.toString();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String authorizationRoles) {
        this.username = username;
        this.password = password;
        this.authorizationRoles = authorizationRoles;
    }

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();

    public void addAccount(Account account) {
        accounts.add(account);
        account.setUser(this);
    }
    public void removeAccount(String currency){
        Account account = getAccountsByCurrency(currency);
        accounts.remove(account);
        account.setUser(null);
    }

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Transfer> transfers = new ArrayList<>();

    public void addTransfer(Transfer transfer) {
        transfers.add(transfer);
        transfer.setUser(this);
    }

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Request> requests = new ArrayList<>();

    public void addRequest(Request request) {
        requests.add(request);
        request.setUser(this);
    }

    public Account getAccountsByCurrency(String accountCurrency) {
        Optional<Account> account = accounts.stream().filter(a -> a.getCurrency().equals(accountCurrency)).findAny();
        if(account.isEmpty()){
            return null;
        }
        return account.get();
    }

    public Request getRequestById(Long id) {
        Optional<Request> request = requests.stream().filter(r -> r.getId().equals(id)).findAny();
        if(request.isEmpty()){
            return null;
        }
        return request.get();
    }

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Exchange> exchanges = new ArrayList<>();
    public void addExchange(Exchange exchange) {
        exchanges.add(exchange);
        exchange.setUser(this);
    }

    public Request getRequestByRequestIdIncome(Long id){
            Optional<Request> request = requests.stream().filter(r -> r.getRequestIncomeId().equals(id)).findAny();
            if(request.isEmpty()) {
                return null;
            }
            return request.get();

    }
}
