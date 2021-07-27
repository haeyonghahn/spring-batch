package com.springframework.batch.config;

public class QuartzConfigExample {

//	@Autowired
//	private JobLauncher jobLauncher;
//
//	@Autowired
//	private JobLocator jobLocator;
//
//	@Bean
//	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
//		JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
//		jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
//		return jobRegistryBeanPostProcessor;
//	}
//
//	@Bean
//	public JobDetailFactoryBean jobDetailFactoryBean() {
//		JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
//		jobDetailFactoryBean.setJobClass(QuartzJobLauncher.class);
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("jobName", "testJob");
//		map.put("jobLauncher", jobLauncher);
//		map.put("jobLocator", jobLocator);
//
//		jobDetailFactoryBean.setJobDataAsMap(map);
//
//		return jobDetailFactoryBean;
//	}
//
//	@Bean
//	public CronTriggerFactoryBean cronTriggerFactoryBean() {
//		CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
//		cronTriggerFactoryBean.setJobDetail(jobDetailFactoryBean().getObject());
//		// rodar de 10 em 10 segundos
//		cronTriggerFactoryBean.setCronExpression("*/50 * * * * ? *");
//
//		return cronTriggerFactoryBean;
//	}
//
//	@Bean
//	public SchedulerFactoryBean schedulerFactoryBean() {
//		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
//		schedulerFactoryBean.setTriggers(cronTriggerFactoryBean().getObject());
//		return schedulerFactoryBean;
//	}
}
