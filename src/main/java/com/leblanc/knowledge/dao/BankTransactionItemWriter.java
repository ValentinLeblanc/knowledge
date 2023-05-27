package com.leblanc.knowledge.dao;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BankTransactionItemWriter implements ItemWriter<BankTransaction> {

	@Autowired
	private BankTransactionRepository bankTransactionRepository;
	
	@Override
	public void write(Chunk<? extends BankTransaction> chunk) throws Exception {
		bankTransactionRepository.saveAll(chunk);
	}
}