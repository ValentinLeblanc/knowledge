package com.leblanc.knowledge.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import com.leblanc.knowledge.dao.BankTransaction;

@Configuration
public class SpringBatchConfig {

	@Value("${inputFile}")
	private Resource inputFile;

	@Bean
	public Job bankJob(JobRepository jobRepository, ItemWriter<BankTransaction> bankTransactionItemWriter,
			PlatformTransactionManager transactionManager,
			ItemProcessor<BankTransaction, BankTransaction> bankTransactionItemProcessor) {
		Step step = new StepBuilder("step-load-data", jobRepository)
				.<BankTransaction, BankTransaction>chunk(100, transactionManager).reader(itemReader())
				.processor(bankTransactionItemProcessor).writer(bankTransactionItemWriter).build();

		return new JobBuilder("bank-data-loader-job", jobRepository).start(step).build();
	}

	@Bean
	public ItemReader<BankTransaction> itemReader() {
		FlatFileItemReader<BankTransaction> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setName("CSV-READER");
		flatFileItemReader.setResource(inputFile);
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;
	}

	@Bean
	public LineMapper<BankTransaction> lineMapper() {
		DefaultLineMapper<BankTransaction> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id", "accountID", "strTransactionDate", "transactionType", "amount");
		lineMapper.setLineTokenizer(lineTokenizer);
		BeanWrapperFieldSetMapper<BankTransaction> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(BankTransaction.class);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		return lineMapper;
	}

}
