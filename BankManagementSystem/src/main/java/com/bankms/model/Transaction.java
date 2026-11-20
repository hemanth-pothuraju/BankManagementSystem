package com.bankms.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
    private int id;
    private int accountId;
    private String accountNumber;
    private String txnType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String relatedAccount;
    private String description;
    private Timestamp txnTime;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getTxnType() { return txnType; }
    public void setTxnType(String txnType) { this.txnType = txnType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    public String getRelatedAccount() { return relatedAccount; }
    public void setRelatedAccount(String relatedAccount) { this.relatedAccount = relatedAccount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Timestamp getTxnTime() { return txnTime; }
    public void setTxnTime(Timestamp txnTime) { this.txnTime = txnTime; }
}
