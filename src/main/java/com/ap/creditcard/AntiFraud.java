package com.ap.creditcard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public interface AntiFraud {

  /**
   * This method filters out all the fraud transactions and return their credit card number in a list. It will throw
   * IllegalAru
   * @param creditCardTransactions list of transaction data which cannot be null.
   * @param transactionDate the transaction date which cannot be null.
   * @param threshold the amount of a fraud transaction, which cannot be negative.
   * @return list of credit card number of fraud transactions
   *
   */
  static List<String> filterFraudTransactions(final List<String> creditCardTransactions, LocalDate transactionDate, BigDecimal threshold) {
    if (creditCardTransactions == null) {
      throw new IllegalArgumentException("creditCardTransactions cannot be null!");
    }
    if (transactionDate == null) {
      throw new IllegalArgumentException("transactionDate cannot be null!");
    }
    if (threshold == null || threshold.doubleValue() < 0) {
      throw new IllegalArgumentException("threshold cannot be negative or null!");
    }

    List<String> fraudCreditCardNos = new ArrayList<>();

    if (creditCardTransactions.isEmpty()) {
      return fraudCreditCardNos;
    }

    Map<String, BigDecimal> txnNoAmountMap = new TreeMap<>();
    creditCardTransactions.stream().map(Transaction::new) // convert txn literal to Transaction instance
        .filter(txn -> txn.getTransactionDateTime().toLocalDate().equals(transactionDate)) // filter by txn date
        .forEach(txn -> { // total up txn amount by txn number
          String txnNo = txn.getCardNumber();
          BigDecimal amount = txnNoAmountMap.get(txnNo);
          if (amount != null) {
            amount = amount.add(txn.getAmount());
          } else {
            amount = txn.getAmount();
          }
          txnNoAmountMap.put(txnNo, amount);
        });

    return txnNoAmountMap.entrySet().stream()
        .filter(txnEntry -> threshold.compareTo(txnEntry.getValue()) <= 0) // filter txn exceeding threshold
        .map(Map.Entry::getKey) // get txn number
        .collect(Collectors.toList()); // collecting txn nos into a list and then return the list
  }
}
