package com.example;

import java.util.Arrays;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class TaskletStepConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job batchJob() {
		return this.jobBuilderFactory.get("batchJob")
			.start(taskStep())
			.next(chunkStep())
			.build();
	}

	@Bean
	public Step taskStep() {
		return stepBuilderFactory.get("taskStep")
			.tasklet((contribution, chunkContext) -> {
				System.out.println("step1 has executed");
				return RepeatStatus.FINISHED;
			})
			.build();
	}
	@Bean
	public Step chunkStep() {
		return stepBuilderFactory.get("chunkStep")
			.<String, String>chunk(3)
			.reader(new ListItemReader(Arrays.asList("item1","item2","item3")))
			.processor(new ItemProcessor<String, String>() {
				@Override
				public String process(String item) throws Exception {
					return item.toUpperCase();
				}
			})
			.writer(list -> {
				list.forEach(item -> System.out.println(item));
			})
			.build();
	}
}
