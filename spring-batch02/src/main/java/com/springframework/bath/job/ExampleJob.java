package com.springframework.bath.job;

//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;

//import com.douzone.comet.batch.sd.tasklet.CheckHolidayTasklet;
//import com.douzone.comet.batch.sd.tasklet.CreateHtmlResourceTasklet;
//import com.douzone.comet.batch.sd.tasklet.ReceiveSlssData;
//import com.douzone.comet.batch.sd.tasklet.SendContractMessageTasklet;

public class ExampleJob {

//	protected JobBuilderFactory jobBuilderFactory;
//	protected StepBuilderFactory stepBuilderFactory;
//	private ReceiveSlssData receiveSlssDataTasklet;
//	private CreateHtmlResourceTasklet createContractPdfTasklet;
//	private SendContractMessageTasklet sendContractMessageTasklet;
//	private CheckHolidayTasklet checkHolidayTasklet;
//	
//	@Autowired
//	public SlsstrSendJob(
//			JobBuilderFactory jobBuilderFactory,
//			StepBuilderFactory stepBuilderFactory,
//			ReceiveSlssData receiveSlssDataTasklet,
//			CreateHtmlResourceTasklet createContractPdfTasklet,
//			SendContractMessageTasklet sendContractMessageTasklet,
//			CheckHolidayTasklet checkHolidayTasklet) {
//		this.jobBuilderFactory = jobBuilderFactory;
//		this.stepBuilderFactory = stepBuilderFactory;
//		this.receiveSlssDataTasklet = receiveSlssDataTasklet;
//		this.createContractPdfTasklet = createContractPdfTasklet;
//		this.sendContractMessageTasklet = sendContractMessageTasklet;
//		this.checkHolidayTasklet = checkHolidayTasklet;
//	}
	
//	@Bean
//	public Job SDX01010Job() {
//		return jobBuilderFactory.get("SDX01010Job")
//				.start(checkHolidayTasklet())
//				.on("FAILED")
//					.end()
//				.from(checkHolidayTasklet())
//				.on("COMPLETED")
//					.to(receiveSlssDataTasklet())
//					.on("COMPLETED")
//						.to(createPdfTasklet())
//						.on("COMPLETED")
//							.to(sendMessageTasklet())
//							.end()
//				.build();
//	}
//	
//	@Bean
//	public Step receiveSlssDataTasklet() {
//		return stepBuilderFactory.get("receiveSlssDataStep")
//				.tasklet(receiveSlssDataTasklet.receiveSlssDataTaskletImpl())
//				.build();
//	}
//	
//	@Bean
//	public Step createPdfTasklet() {
//		return stepBuilderFactory.get("createPdfStep")
//				.tasklet(createContractPdfTasklet.createHtmlResourceTaskletImpl())
//				.build();
//	}
//	
//	@Bean
//	public Step sendMessageTasklet() {
//		return stepBuilderFactory.get("sendMessageStep")
//				.tasklet(sendContractMessageTasklet.sendMessageTaskletImpl())
//				.build();
//	}
//	
//	public Step checkHolidayTasklet() {
//		return stepBuilderFactory.get("checkHolidayStep")
//				.tasklet(checkHolidayTasklet.checkHolidayTaskletImpl())
//				.build();
//	}
	
}

