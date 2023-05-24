package com.leblanc.knowledge.dao;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class BankTransaction {

	@Id
	private Long id;
	private Long accountID;
	private Date transactionDate;
	@Transient
	private String strTransactionDate;
	private String transactionType;
	private double amount;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getAccountID() {
		return accountID;
	}
	public void setAccountID(Long accountID) {
		this.accountID = accountID;
	}
	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getStrTransactionDate() {
		return strTransactionDate;
	}
	public void setStrTransactionDate(String strTransactionDate) {
		this.strTransactionDate = strTransactionDate;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public BankTransaction() {
	}
	
	public BankTransaction(Long id, Long accountID, Date transactionDate, String strTransactionDate,
			String transactionType, double amount) {
		super();
		this.id = id;
		this.accountID = accountID;
		this.transactionDate = transactionDate;
		this.strTransactionDate = strTransactionDate;
		this.transactionType = transactionType;
		this.amount = amount;
	}
	@Override
	public String toString() {
		return "BankTransaction [id=" + id + ", accountID=" + accountID + ", transactionDate=" + transactionDate
				+ ", strTransactionDate=" + strTransactionDate + ", transactionType=" + transactionType + ", amount="
				+ amount + "]";
	}
	
}
