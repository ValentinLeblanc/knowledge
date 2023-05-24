package com.leblanc.knowledge.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class BankTransactionItemProcessor implements ItemProcessor<BankTransaction, BankTransaction> {

	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
	
	@Override
	public BankTransaction process(BankTransaction bankTransaction) throws Exception {
		bankTransaction.setTransactionDate(dateFormat.parse(bankTransaction.getStrTransactionDate()));
		return bankTransaction;
	}
}