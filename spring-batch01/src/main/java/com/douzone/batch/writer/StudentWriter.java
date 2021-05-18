package com.douzone.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.douzone.batch.models.Students;

public class StudentWriter implements ItemWriter<Students> {

	@Override
	public void write(List<? extends Students> items) throws Exception {

		for(Students item : items){
			System.out.println(item);
		}
	}

}
