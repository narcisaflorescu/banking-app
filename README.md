The bank application have the following features:

The users:
- Have the possibility to login / logout – Use Spring Security
- Be able to have multiple accounts in different currencies
- Be able to see the available balance for all the accounts
- Be able to exchange money from a currency to another (Use an HTTP Client (Rest Template) to call an external api to fetch the exchange rate.)
- Be able to make a transaction to another user
- Be able to request money from another user
- Be able to see the transactions history (income/ outcome)

Transaction:
- An operation of money transfer between two accounts
- After the transaction is executed, the balance is updated accordingly for both accounts

It uses a postman collection which contains all my created endpoints.
