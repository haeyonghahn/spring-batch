package com.example;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class FlatFilesDelimitedConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("batchJob")
            .start(step1())
            .next(step2())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<String, String>chunk(3)
            .reader(itemReader())
            .writer(new ItemWriter() {
                @Override
                public void write(List items) throws Exception {
                    items.forEach(item -> System.out.println(item));
                }
            })
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet((contribution, chunkContext) -> {
                System.out.println("step2 has executed");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    public FlatFileItemReader itemReader() {
        return new FlatFileItemReaderBuilder<Customer>()
            .name("flatFile")
            .resource(new ClassPathResource("customer.csv"))
            .fieldSetMapper(new CustomerFieldSetMapper())
            // .targetType(Customer.class)
            .linesToSkip(1)
            .delimited().delimiter(",")
            .names("name", "age", "year")
            .build();
    }

    public FlatFileItemReader itemReader2() {
        return new FlatFileItemReaderBuilder<Customer>()
            .name("flatFile")
            .resource(new ClassPathResource("customer.csv"))
            .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
            .targetType(Customer.class)
            .linesToSkip(1)
            .delimited().delimiter(",")
            .names("name","year","age")
            .build();
    }
}
