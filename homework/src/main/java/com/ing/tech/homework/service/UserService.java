package com.ing.tech.homework.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ing.tech.homework.dto.*;
import com.ing.tech.homework.model.*;
import com.ing.tech.homework.model.Currency;
import com.ing.tech.homework.repository.*;
import com.ing.tech.homework.security.JwtProvider;
import com.ing.tech.homework.service.exceptions.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.ing.tech.homework.model.Currency.*;
import static com.ing.tech.homework.model.StateRequest.*;
import static com.ing.tech.homework.model.UserState.INACTIVE;


@Service
@AllArgsConstructor
@Slf4j

public class UserService {

    private UserRepository userRepository;
    private AccountRepository accountRepository;
    private RequestRepository requestRepository;
    private ModelMapper modelMapper;
    public UserDtoPost save(UserDtoPost userDto) {

        Optional<User> userOptional = userRepository.findUserByUsername(userDto.getUsername());

        if (userOptional.isPresent()) {
            throw new UsernameAlreadyTakenException();
        } else {
            User user = userRepository.save(
                    modelMapper.map(userDto, User.class)
            );

            return modelMapper.map(user, UserDtoPost.class);
        }
    }

    public void createAccountInUser(String username, AccountDtoPost accountDtoPost) {

        User user = userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        Account account = user.getAccountsByCurrency(accountDtoPost.getCurrency());

        if(account == null) {

            checkCurrency(accountDtoPost.getCurrency());
            user.addAccount(modelMapper.map(accountDtoPost,Account.class));
            userRepository.save(user);

        }else{
            throw new UserAlreadyHasProvidedCurrencyAccount();
        }

    }

    public List<UserDtoGetAll> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(e -> modelMapper.map(e, UserDtoGetAll.class)).collect(Collectors.toList());
    }

    public UserDtoGet getUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, UserDtoGet.class);
    }

    public Double getAccountBalance(String username, String accountCurrency) {
        User user = userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        checkCurrency(accountCurrency);
        Account account = user.getAccountsByCurrency(accountCurrency);
        if (account == null) {
            throw new AccountNotFoundException();
        }
        return account.getBalance();
    }

    public String makeInactiveUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        user.setState(INACTIVE.toString());
        userRepository.save(user);

        return null;
        //userRepository.delete(user);
    }
    public void deleteUserAccountByCurrency(String username, String currency) {
        User user = userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        checkCurrency(currency);
        Account account = user.getAccountsByCurrency(currency);

        if(account == null){
            throw new AccountNotFoundException();
        }else {
            user.removeAccount(currency);
            accountRepository.deleteById(account.getId());
        }
    }
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        if(user.getState().equals(INACTIVE.toString())) {
            userRepository.deleteById(id);
        }else {
            throw new TransactionException();
        }

    }


    // ------------------------------ TRANSFER MONEY ---------------------------------------------------

    public void transferMoney(String senderUsername, TransferDtoPost transferDtoPost) {

        if(senderUsername.equals(transferDtoPost.getReceiverUsername())){
            throw new TransactionException();
        }

        User senderUser = userRepository.findUserByUsername(senderUsername).orElseThrow(UserNotFoundException::new);
        User receiverUser = userRepository.findUserByUsername(transferDtoPost.getReceiverUsername()).orElseThrow(UserNotFoundException::new);

        if(receiverUser.getState().equals(INACTIVE.toString())){
            throw new TransactionException();
        }
        checkCurrency(transferDtoPost.getCurrency());
        Account senderAccount = senderUser.getAccountsByCurrency(transferDtoPost.getCurrency());
        Account receiverAccount = receiverUser.getAccountsByCurrency(transferDtoPost.getCurrency());

        if(senderAccount == null || receiverAccount == null)
            throw new AccountNotFoundException();

        if (transferDtoPost.getAmount() < 0 || senderAccount.getBalance() < transferDtoPost.getAmount()) {
            throw new TransactionException();
        }

        Double newBalanceSender = senderAccount.getBalance() - transferDtoPost.getAmount();
        Double newBalanceReceiver = receiverAccount.getBalance() + transferDtoPost.getAmount();

        senderAccount.setBalance(newBalanceSender);
        receiverAccount.setBalance(newBalanceReceiver);

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        Transfer transfer = new Transfer(senderUsername, transferDtoPost.getReceiverUsername(),
                transferDtoPost.getCurrency(), transferDtoPost.getAmount());

        transfer.setDate(LocalDateTime.now());

        Transfer transferIncome = transfer;
        transferIncome.setType("income");
        receiverUser.addTransfer(transferIncome);
        userRepository.save(receiverUser);


        Transfer transferOutcome = transfer;
        transferOutcome.setType("outcome");
        senderUser.addTransfer(transferOutcome);
        userRepository.save(senderUser);

    }

    public List<TransferDtoGet> getTransfers(String username, String type) {
        User user = userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        List<TransferDtoGet> transfers = user.getTransfers().stream()
                .filter(t -> t.getType().equals(type))
                .sorted(Comparator.comparing(Transfer::getDate).reversed())
                .map(e -> modelMapper.map(e, TransferDtoGet.class))
                    .collect(Collectors.toList());
        return transfers;
    }



    // ------------------------------ REQUEST MONEY ---------------------------------------------------

    public void requestMoney(String fromUsername, RequestDtoPost requestDtoPost) {

        if(fromUsername.equals(requestDtoPost.getToUsername())){
            throw new TransactionException();
        }

        User fromUser = userRepository.findUserByUsername(fromUsername).orElseThrow(UserNotFoundException::new);
        User toUser = userRepository.findUserByUsername(requestDtoPost.getToUsername()).orElseThrow(UserNotFoundException::new);

        if(toUser.getState().equals(INACTIVE.toString())){
            throw new TransactionException();
        }


        checkCurrency(requestDtoPost.getCurrency());

        Account fromAccount = fromUser.getAccountsByCurrency(requestDtoPost.getCurrency());
        Account toAccount = toUser.getAccountsByCurrency(requestDtoPost.getCurrency());

        if(fromAccount == null || toAccount == null) {
            throw new AccountNotFoundException();
        }

        Request requestIncome = new Request(fromUsername, requestDtoPost.getToUsername(), requestDtoPost.getCurrency(), requestDtoPost.getAmount());
        requestIncome.setDate(LocalDateTime.now());
        requestIncome.setType("income");
        requestIncome.setState(IN_PROGRESS.toString());
        toUser.addRequest(requestIncome);
        Request requestIncomeFromDb = requestRepository.save(requestIncome);
        userRepository.save(toUser);

        Request requestOutcome = new Request(fromUsername, requestDtoPost.getToUsername(), requestDtoPost.getCurrency(), requestDtoPost.getAmount());
        requestOutcome.setDate(LocalDateTime.now());
        requestOutcome.setType("outcome");
        requestOutcome.setState(IN_PROGRESS.toString());
        fromUser.addRequest(requestOutcome);
        requestOutcome.setRequestIncomeId(requestIncomeFromDb.getId());
        userRepository.save(fromUser);



    }

    public List<RequestDtoGetAccept> getRequests (String username, String type) {
        User user = userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        List<RequestDtoGetAccept> requests = user.getRequests().stream()
                .filter(r -> r.getType().equals(type))
                .sorted(Comparator.comparing(Request::getDate).reversed())
                .map(e -> modelMapper.map(e, RequestDtoGetAccept.class))
                .collect(Collectors.toList());
        return requests;
    }

    // ------------------------------ ACCEPT/REJECT REQUEST  ---------------------------------------------------

    public RequestDtoGet acceptOrRejectRequest(String username, String type, Long id, String state) {
        User userReceiver = userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        Request requestIncome = userReceiver.getRequestById(id);

        if (requestIncome == null) {
            throw new RequestNotFoundException();
        }
        if (requestIncome.getType().equals(type)) {

            checkRequestState(state);

            User userSender = userRepository.findUserByUsername(requestIncome.getFromUsername()).orElseThrow(UserNotFoundException::new);
            Request requestOutcome = userSender.getRequestByRequestIdIncome(id);

            if(requestIncome.getState().equals(IN_PROGRESS.toString())) {

                switch (StateRequest.valueOf(state)) {
                    case ACCEPT: {
                        try {
                            TransferDtoPost transferDtoPost = new TransferDtoPost(requestIncome.getFromUsername(), requestIncome.getCurrency(), requestIncome.getAmount());
                            transferMoney(userReceiver.getUsername(), transferDtoPost);

                            requestIncome.setState(ACCEPT.toString());
                            requestOutcome.setState(ACCEPT.toString());
                            requestRepository.save(requestIncome);
                            requestRepository.save(requestOutcome);

                        }catch (TransactionException e){

                            requestIncome.setState(REJECT.toString());
                            requestOutcome.setState(REJECT.toString());
                            requestRepository.save(requestIncome);
                            requestRepository.save(requestOutcome);
                        }

                        break;
                    }
                    case REJECT: {
                        requestIncome.setState(REJECT.toString());
                        requestOutcome.setState(REJECT.toString());
                        requestRepository.save(requestIncome);
                        requestRepository.save(requestOutcome);
                        break;
                    }
                    default:
                        throw new TransactionException();
                }
            }else{
                throw new TransactionException();
            }
        }else{
            throw new RequestNotFoundException();
        }
        return modelMapper.map(requestIncome, RequestDtoGet.class);
    }


    // ------------------------------ EXCHANGE MONEY ---------------------------------------------------

    public void exchange(String username, ExchangeDtoPost exchangeDtoPost){
        User user = userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        checkCurrency(exchangeDtoPost.getFromCurrency());
        checkCurrency(exchangeDtoPost.getToCurrency());

        Account fromAccount = user.getAccountsByCurrency(exchangeDtoPost.getFromCurrency());
        Account toAccount = user.getAccountsByCurrency(exchangeDtoPost.getToCurrency());

        if(fromAccount == null){
            throw new AccountNotFoundException();
        }
        if(toAccount == null){
            throw new AccountNotFoundException();
        }
        makeExchange(fromAccount, toAccount, exchangeDtoPost.getAmount());

        Exchange exchange = modelMapper.map(exchangeDtoPost, Exchange.class);
        exchange.setDate(LocalDateTime.now());

        user.addExchange(exchange);

        userRepository.save(user);



    }

    private void makeExchange(Account fromAccount, Account toAccount, Double amount){

        String fromCurrency = fromAccount.getCurrency();
        String toCurrency = toAccount.getCurrency();

        if(amount > fromAccount.getBalance()){
            throw new TransactionException();
        }else{
            switch (Currency.valueOf(fromCurrency)){
                case EUR:
                    if(Currency.valueOf(toCurrency).equals(RON)){
                        Double exchangeRateFromEURtoRON = getExchangeRateFromCurrencyToExchangedCurrencyValue(EUR.toString(), RON.toString());
                        exchangeAmountFromAccountToAccountWithRate(amount, fromAccount, toAccount, exchangeRateFromEURtoRON);
                        break;
                    }
                    if(Currency.valueOf(toCurrency).equals(USD)){
                        Double exchangeRateFromEURtoUSD = getExchangeRateFromCurrencyToExchangedCurrencyValue(EUR.toString(), USD.toString());
                        exchangeAmountFromAccountToAccountWithRate(amount, fromAccount, toAccount, exchangeRateFromEURtoUSD);
                        break;
                    }
                    throw new TransactionException();

                case RON:
                    if(Currency.valueOf(toCurrency).equals(EUR)){
                        Double exchangeRateFromRONtoEUR = getExchangeRateFromCurrencyToExchangedCurrencyValue(RON.toString(), EUR.toString());
                        exchangeAmountFromAccountToAccountWithRate(amount, fromAccount, toAccount, exchangeRateFromRONtoEUR);
                        break;
                    }
                    if(Currency.valueOf(toCurrency).equals(USD)){
                        Double exchangeRateFromRONtoUSD = getExchangeRateFromCurrencyToExchangedCurrencyValue(RON.toString(), USD.toString());
                        exchangeAmountFromAccountToAccountWithRate(amount, fromAccount, toAccount, exchangeRateFromRONtoUSD);
                        break;
                    }
                    throw new TransactionException();

                case USD:
                    if(Currency.valueOf(toCurrency).equals(EUR)){
                        Double exchangeRateFromUSDtoEUR = getExchangeRateFromCurrencyToExchangedCurrencyValue(USD.toString(), EUR.toString());
                        exchangeAmountFromAccountToAccountWithRate(amount, fromAccount, toAccount, exchangeRateFromUSDtoEUR);
                        break;
                    }
                    if(Currency.valueOf(toCurrency).equals(RON)){
                        Double exchangeRateFromUSDtoRON = getExchangeRateFromCurrencyToExchangedCurrencyValue(USD.toString(), RON.toString());
                        exchangeAmountFromAccountToAccountWithRate(amount, fromAccount, toAccount, exchangeRateFromUSDtoRON);
                        break;
                    }
                    throw new TransactionException();

                default:
                    throw new TransactionException();
            }
        }
    }

    private void exchangeAmountFromAccountToAccountWithRate(Double amount, Account fromAccount, Account toAccount, Double rate) {
        toAccount.setBalance(toAccount.getBalance() + rate * amount);
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        accountRepository.save(toAccount);
        accountRepository.save(fromAccount);

    }
    private Double getExchangeRateFromCurrencyToExchangedCurrencyValue(String currency, String exchangedCurrencyValue) {
            String data = null;
            StringBuilder responseData = new StringBuilder();
            JsonObject jsonObject = null;
            URL url = null;
            HttpURLConnection con = null;
        try {
            url = new URL("https://api.exchangerate-api.com/v4/latest/" + currency);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
        }catch (IOException e ){
            throw new ExchangeRateException();
        }
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                responseData.append(line);
            }
            jsonObject = new Gson().fromJson(responseData.toString(), JsonObject.class);

            data = jsonObject.get("rates").toString();

        }catch (IOException e){
            throw new ExchangeRateException();
        }
        Double exchangedValue= null;
        try {
            int statOfString = data.indexOf(exchangedCurrencyValue);
            String substring = data.substring(statOfString);
            String value = substring.substring(5, substring.indexOf(","));
            exchangedValue = Double.valueOf(value);
        }catch (IndexOutOfBoundsException |  NumberFormatException e){
            throw new ExchangeRateException();
        }
        return exchangedValue;

    }

    private void checkCurrency(String currency){

        try {
            Currency.valueOf(currency);
        }catch (IllegalArgumentException e){
            throw new StringToEnumConvertorException();
        }
    }
    private void checkRequestState(String state){
        try{
            StateRequest.valueOf(state);
        }catch (IllegalArgumentException e){
            throw new StringToEnumConvertorException();
        }
    }

    public List<ExchangeDtoGet> getExchanges(String username) {
        User user = userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);

        List<Exchange> exchanges = user.getExchanges();

        return exchanges.stream()
                .sorted(Comparator.comparing(Exchange::getDate).reversed())
                .map(e -> modelMapper.map(e, ExchangeDtoGet.class))
                .collect(Collectors.toList());
    }


    public List<TransactionInterface> getUserHistory(String username) {
        User user = userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);

        List<Exchange> exchanges = user.getExchanges();
        List<Transfer> transfers = user.getTransfers();

        List<ExchangeDtoGet> exchangeDtoGets = exchanges.stream().map(e -> modelMapper.map(e, ExchangeDtoGet.class)).collect(Collectors.toList());
        List<TransferDtoGet> transferDtoGets = transfers.stream().map(e -> modelMapper.map(e, TransferDtoGet.class)).collect(Collectors.toList());

        List<TransactionInterface> transactionsHistory = new ArrayList<>();
        transactionsHistory.addAll(transferDtoGets);
        transactionsHistory.addAll(exchangeDtoGets);


        return transactionsHistory.stream()
                .sorted(Comparator.comparing(TransactionInterface::getDate).reversed())
                .collect(Collectors.toList());

    }


    // ------------------------------ SECURITY---------------------------------------------------

    private JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private static final long TTL = 60;

    public void create(UserDtoPost userDto) {

        Optional<User> userFromDb = userRepository.findUserByUsername(userDto.getUsername());
        if(userFromDb.isPresent()){
            throw new UsernameAlreadyTakenException();
        }
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        User user = new User(userDto.getUsername(), hashedPassword, userDto.getAuthorizationRoles());
        userRepository.save(user);

    }

    public String authenticate(UserDtoPost userDto) {
        User user = userRepository.findUserByUsername(userDto.getUsername()).orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException();
        }
        if(user.getState().equals(INACTIVE.toString())){
            throw new UnauthorizedException();
        }

        Set<String> roles = Arrays.stream(user.getAuthorizationRoles().split(",")).collect(Collectors.toSet());
        return jwtProvider.generateToken(user.getUsername(), TTL, roles);
    }



}
