package com.springframework.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.springframework.batch.items.CustomItemReader;
import com.springframework.batch.items.CustomItemWriter;
import com.springframework.batch.tasklet.SampleTasklet01;
import com.springframework.batch.tasklet.SampleTasklet03;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
//	@Autowired
//	public DataSourceTransactionManager transactionManager;

	@Bean("testJob01")
	public Job testJob01() {
		return jobBuilderFactory.get("testJob01")
				.start(step01())
					.on("FAILED")
					.fail()
					.on("*")
					.to(sizeDecider())
				.from(sizeDecider())
					.on("ZERO")
					.end()
				.from(sizeDecider())
					.on("NOT ZERO")
					.to(step03())
					.on("*")
					.end()
				.end()
				.build();
	}

	@Bean
	public Step step01() {
		return stepBuilderFactory.get("step01")
				.tasklet(sampleTasklet01())
				.allowStartIfComplete(false)
//				.transactionManager(transactionManager)
				.build();
	}
	
	@Bean
	public Step step03() {
		return stepBuilderFactory.get("step03")
				.tasklet(sampleTasklet03())
				.allowStartIfComplete(false)
//				.transactionManager(transactionManager)
				.build();
	}
	
	@Bean
	public SizeDecider sizeDecider() {
		return new SizeDecider();
	}
	
	@Bean
	public SampleTasklet01 sampleTasklet01() {
		
		return new SampleTasklet01();
	}
	
	@Bean
	public SampleTasklet03 sampleTasklet03() {
		return new SampleTasklet03();
	}
	
	public class SizeDecider implements JobExecutionDecider {
		@Override
		public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
			ExecutionContext jobContext = jobExecution.getExecutionContext();
			int size = -1;
			if(jobContext.get("SIZE") != null) {				
				size =  (int) jobContext.get("SIZE");
			}
			if(size == 0) {
				return new FlowExecutionStatus("ZERO");
			}
			else {
				return new FlowExecutionStatus("NOT ZERO");
			}
		}
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

