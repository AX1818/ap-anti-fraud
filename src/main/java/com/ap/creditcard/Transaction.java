package com.ap.creditcard;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The POJO class for a transaction record.
 */
public class Transaction {
  private String cardNumber;

  private LocalDateTime transactionDateTime;

  private BigDecimal amount;

  public Transaction(String transactionData) {
    List<String> parts = Arrays.stream(transactionData.split(",")).map(String::trim).collect(Collectors.toList());
    this.cardNumber = parts.get(0);
    this.transactionDateTime = LocalDateTime.parse(parts.get(1), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    this.amount = new BigDecimal(parts.get(2));
  }

  public String getCardNumber() {
    return cardNumber;
  }


  public LocalDateTime getTransactionDateTime() {
    return transactionDateTime;
  }

  public BigDecimal getAmount() {
    return amount;
  }

}
