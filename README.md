## Solution
The interface `com.ap.creditcard.AntiFraud` is designed to provide a static method `static List<String> filterFraudTransactions(final List<String> creditCardTransactions, LocalDate transactionDate, BigDecimal threshold)`
which can determine if a transaction is a fraud one by the specified transaction date and the threshold for that day.  

This method uses the steam api in Java 8 to handle the transaction list. It has four steps as below:
1. convert transaction literal data into instances of `com.ap.creditcard.Transaction` class.
2. Filter out all the transaction data by comparing their transaction date with the specified date.
3. Total up the transaction amount by the transaction number via a <transaction no, total amount> map.
4. streaming the map entries to collect the keys, i.e. transaction numbers into a list and then return it.

## Run tests
The tests are in `com.ap.creditcard.AntiFraudTest` class. To run the tests, just execute the maven command:
```bash
mvn test
``` 
