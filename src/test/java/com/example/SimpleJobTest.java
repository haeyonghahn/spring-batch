package com.example;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBatchTest
@RunWith(SpringRunner.class)
@SpringBootTest(classes={SimpleJobConfiguration.class, TestBatchConfig.class})
public class SimpleJobTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	public void simple_job_테스트() throws Exception {
		//given
		JobParameters jobParameters = new JobParametersBuilder()
			.addString("requestDate", "20020101")
			.addLong("date", new Date().getTime())
			.toJobParameters();

		//when
		// JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
		JobExecution jobExecution1 = jobLauncherTestUtils.launchStep("step1");

		//then
		// assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
		// assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);

		StepExecution stepExecution = (StepExecution)((List<?>)jobExecution1.getStepExecutions()).get(0);

		assertEquals(stepExecution.getCommitCount(), 11);
		assertEquals(stepExecution.getReadCount(), 1000);
		assertEquals(stepExecution.getWriteCount(), 1000);
	}

	@After
	public void clear() {
		jdbcTemplate.execute("delete from customer2");
	}
}
