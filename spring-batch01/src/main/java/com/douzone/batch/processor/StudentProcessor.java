package com.douzone.batch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.douzone.batch.models.Students;

public class StudentProcessor implements ItemProcessor<Students,Students> {

    @Override
    public Students process(Students student) throws Exception {
      return student != null ? student : null;
    }
}
