package com.springframework.batch.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private JobLocator jobLocator;

	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
		JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
		jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);

		return jobRegistryBeanPostProcessor;
	}

	@Bean
	public JobDetailFactoryBean jobDetailFactoryBean01() {
		JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
		jobDetailFactoryBean.setJobClass(QuartzJobLauncher.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jobName", "testJob01");
		map.put("jobLauncher", jobLauncher);
		map.put("jobLocator", jobLocator);

		jobDetailFactoryBean.setJobDataAsMap(map);

		return jobDetailFactoryBean;
	}
	
	@Bean
	public JobDetailFactoryBean jobDetailFactoryBean02() {
		JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
		jobDetailFactoryBean.setJobClass(QuartzJobLauncher.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jobName", "testJob02");
		map.put("jobLauncher", jobLauncher);
		map.put("jobLocator", jobLocator);

		jobDetailFactoryBean.setJobDataAsMap(map);

		return jobDetailFactoryBean;
	}

	@Bean
	public CronTriggerFactoryBean cronTriggerFactoryBean01() {
		CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
		cronTriggerFactoryBean.setJobDetail(jobDetailFactoryBean01().getObject());
		// 매 10초마다 실행
		cronTriggerFactoryBean.setCronExpression("*/10 * * * * ? *");
		// 매 2분마다 실행
//		cronTriggerFactoryBean.setCronExpression("0 0/2 * ?");

		return cronTriggerFactoryBean;
	}
	
	@Bean
	public SimpleTriggerFactoryBean simpleTriggerFactoryBean02() {
		SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
		simpleTriggerFactoryBean.setJobDetail(jobDetailFactoryBean01().getObject());
		// 시작하고 1분 후에 실행
		simpleTriggerFactoryBean.setStartDelay(60000);
		// 매 24시간마다 실행
		simpleTriggerFactoryBean.setRepeatInterval(864000000);
		
		return simpleTriggerFactoryBean;
	}

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setTriggers(cronTriggerFactoryBean01().getObject());
		schedulerFactoryBean.setTriggers(simpleTriggerFactoryBean02().getObject());

		return schedulerFactoryBean;
	}
}
