package com.springframework.batch.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springframework.batch.models.Employees;
import com.springframework.batch.repository.BatchRepository;

@Service
public class BatchService {

	@Autowired
	private BatchRepository batchRepository;
	
	public List<Employees> select_employees() {
		return batchRepository.select_employees();
	}
}
