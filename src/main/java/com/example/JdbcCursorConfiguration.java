package com.example;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class JdbcCursorConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("batchJob")
            .incrementer(new RunIdIncrementer())
            .start(step1())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<Customer, Customer>chunk(10)
            .reader(customerItemReader())
            .writer(customItemWriter())
            .build();
    }

    @Bean
    public JdbcCursorItemReader<Customer> customerItemReader() {
        return new JdbcCursorItemReaderBuilder<Customer>()
            .name("jdbcCursorItemReader")
            .fetchSize(10)
            .sql("select id, firstName, lastName, birthdate from customer where firstName like ? order by lastName, firstName")
            .beanRowMapper(Customer.class)
            .queryArguments("A%")
            .maxItemCount(3)
            .currentItemCount(2)
            .maxRows(100)
            .dataSource(dataSource)
            .build();
    }

    @Bean
    public ItemWriter<Customer> customItemWriter() {
        return items -> {
            for (Customer item : items) {
                System.out.println(item.toString());
            }
        };
    }
}
