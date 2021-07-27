package com.springframework.batch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springframework.batch.models.Employees;
import com.springframework.batch.service.BatchService;

@Controller
public class BatchController {
	
	@Autowired
	private BatchService batchService;
	
	@RequestMapping({"/", "/batch"})
	public String batch() {
		List<Employees> employeeList = batchService.select_employees();
		System.out.println(employeeList);
		return "batch_scheduler";
	}
}
