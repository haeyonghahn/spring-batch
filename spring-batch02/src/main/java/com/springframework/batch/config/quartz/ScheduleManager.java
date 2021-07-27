package com.springframework.batch.config.quartz;

public class ScheduleManager {

//	// quartz
//	private SchedulerFactory schedulerFactory;
//	private Scheduler scheduler;
//
//	@PostConstruct
//	public void start() throws SchedulerException {
//		schedulerFactory = new StdSchedulerFactory();
//		scheduler = schedulerFactory.getScheduler();
//		scheduler.start();
//
//		// job 지정
//		JobDetail job = JobBuilder.newJob(SampleJob.class).withIdentity("testJob").build();
//
//		// trigger 지정
//		Trigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule("* /1 * * * ?")).build();
//
//		scheduler.scheduleJob(job, trigger);
//	}
}
