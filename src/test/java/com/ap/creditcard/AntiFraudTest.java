package com.ap.creditcard;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AntiFraudTest {
  private List<String> txns;

  @Before
  public void setUp() throws Exception {
    txns = Arrays.asList(
          "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00",
          // totals up to 40.00
          "20d7ce2f43e35fa57d1bbf8b1e3, 2014-04-29T13:15:54, 10.00",
          "20d7ce2f43e35fa57d1bbf8b1e3, 2014-04-29T13:15:54, 30.00",
          // totals up to 30.00
          "40d7ce2f43e35fa57d1bbf8b1e4, 2014-04-29T13:15:54, 10.00",
          "40d7ce2f43e35fa57d1bbf8b1e4, 2014-04-29T13:15:54, 10.00",
          "40d7ce2f43e35fa57d1bbf8b1e4, 2014-04-29T13:15:54, 10.00",
          // 50 on 2014-04-28
          "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-28T13:15:54, 50.00",
          // 100
          "50d7ce2f43e35fa57d1bbf8b1e5, 2014-04-28T13:15:54, 100.00"
      );
  }

  @Test
  public void test_Empty_Txn_List_Return_Empty_TxnNo_List() {
    List<String> txnNos = AntiFraud.filterFraudTransactions(new ArrayList<>(),
        LocalDate.parse("2014-04-29T13:15:54", DateTimeFormatter.ISO_LOCAL_DATE_TIME), new BigDecimal(10));
    assertTrue(txnNos != null);
    assertTrue(txnNos.isEmpty()); 
  }

  @Test
  public void test_Pass_One_NonFraud_TxnReturn_Empty_List() {
    List<String> txnNos = AntiFraud.filterFraudTransactions(Arrays.asList(txns.get(0)),
        LocalDate.parse("2017-01-14T13:15:54", DateTimeFormatter.ISO_LOCAL_DATE_TIME), new BigDecimal(10.00));
    assertTrue(txnNos.isEmpty());
  }

  @Test
  public void test_Pass_All_NonFraud_Txns_Return_Empty_List() {
    List<String> txnNos = AntiFraud.filterFraudTransactions(txns,
        LocalDate.parse("2017-01-14T13:15:54", DateTimeFormatter.ISO_LOCAL_DATE_TIME), new BigDecimal(Integer.MAX_VALUE));
    assertTrue(txnNos.isEmpty());
  }

  @Test
  public void test_Pass_NoTxnDate_Return_Empty_TxnNo_List() {
    // Date '2017-01-14T13:15:54' has no txn in the passed-in list
    List<String> txnNos = AntiFraud.filterFraudTransactions(new ArrayList<>(),
        LocalDate.parse("2017-01-14T13:15:54", DateTimeFormatter.ISO_LOCAL_DATE_TIME), new BigDecimal(10));
    assertTrue(txnNos != null);
    assertTrue(txnNos.isEmpty());
  }

  @Test
  public void test_Return_Fraud_TxnNo_List_By_Date() {
    // There is only on record on this date with amount 100
    LocalDate txnDate = LocalDate.parse("2014-04-28");
    List<String> txnNos = AntiFraud.filterFraudTransactions(txns, txnDate, new BigDecimal(60));
    assertTrue(txnNos.contains("50d7ce2f43e35fa57d1bbf8b1e5"));
    assertTrue(txnNos.size() == 1);

    // set threshold to 40 to return 2 fraud txns on 2014-04-28
    txnNos = AntiFraud.filterFraudTransactions(txns, txnDate, new BigDecimal(40));
    assertTrue(txnNos.contains("10d7ce2f43e35fa57d1bbf8b1e2"));
    assertTrue(txnNos.contains("50d7ce2f43e35fa57d1bbf8b1e5"));
    assertTrue(txnNos.size() == 2);

    txnDate = LocalDate.parse("2014-04-29");
    txnNos = AntiFraud.filterFraudTransactions(txns, txnDate, new BigDecimal(10));
    assertFalse(txnNos.contains("50d7ce2f43e35fa57d1bbf8b1e5"));
    assertTrue(txnNos.size() > 1);
  }

  @Test
  public void test_Return_One_Fraud_TxnNo_In_List_By_Date_And_Threshold() {
    LocalDate txnDate = LocalDate.parse("2014-04-29");
    // 30 will spot '20d7ce2f43e35fa57d1bbf8b1e3'
    List<String> txnNos = AntiFraud.filterFraudTransactions(txns, txnDate, new BigDecimal(35));
    assertTrue(txnNos.contains("20d7ce2f43e35fa57d1bbf8b1e3"));
    assertTrue(txnNos.size() == 1);
  }

  @Test
  public void test_Return_Two_Fraud_TxnNos_In_List_By_Date_And_Threshold() {
    LocalDate txnDate = LocalDate.parse("2014-04-29");
    // 30 will spot '20d7ce2f43e35fa57d1bbf8b1e3'
    List<String> txnNos = AntiFraud.filterFraudTransactions(txns, txnDate, new BigDecimal(30));
    assertTrue(txnNos.contains("20d7ce2f43e35fa57d1bbf8b1e3"));
    assertTrue(txnNos.contains("40d7ce2f43e35fa57d1bbf8b1e4"));
    assertTrue(txnNos.size() == 2);
  }

  @Test
  public void test_Return_All_Fraud_TxnNos_In_List_By_Date_And_Threshold() {
    LocalDate txnDate = LocalDate.parse("2014-04-29");
    // 0 will spot all Txns
    List<String> txnNos = AntiFraud.filterFraudTransactions(txns, txnDate, new BigDecimal(0));
    assertTrue(txnNos.contains("10d7ce2f43e35fa57d1bbf8b1e2"));
    assertTrue(txnNos.contains("20d7ce2f43e35fa57d1bbf8b1e3"));
    assertTrue(txnNos.contains("40d7ce2f43e35fa57d1bbf8b1e4"));
    assertTrue(txnNos.size() == 3);
  }

}