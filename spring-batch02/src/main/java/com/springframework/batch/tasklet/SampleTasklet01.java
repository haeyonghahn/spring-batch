package com.springframework.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class SampleTasklet01 implements Tasklet {

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		System.out.println("####### SampleTasklet01");
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("SIZE", 0);
		return RepeatStatus.FINISHED;
	}
	
	private String getParamValue(ChunkContext chunkContext, String key) {
		return chunkContext.getStepContext().getStepExecution().getJobParameters().getString(key);
	}
}
