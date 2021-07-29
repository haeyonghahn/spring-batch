package com.springframework.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.springframework.batch.items.CustomItemReader;
import com.springframework.batch.items.CustomItemWriter;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job testJob01() {
		return jobBuilderFactory.get("testJob01")
				.incrementer(new RunIdIncrementer())
				.flow(step01())
				.end()
				.build();
	}

	@Bean
	public Step step01() {
		return stepBuilderFactory.get("step01")
				.<Object, Object> chunk(10)
				.reader(reader())
				.writer(writer())
				.build();
	}
	
	@Bean
	public Job testJob02() {
		return jobBuilderFactory.get("testJob02")
				.incrementer(new RunIdIncrementer())
				.flow(step02())
				.end()
				.build();
	}

	@Bean
	public Step step02() {
		return stepBuilderFactory.get("step02")
				.<Object, Object> chunk(10)
				.reader(reader())
				.writer(writer())
				.build();
	}
	
	@Bean
	public CustomItemReader reader() {
		return new CustomItemReader();
	}

	@Bean
	public CustomItemWriter writer() {
		return new CustomItemWriter();
	}
}

