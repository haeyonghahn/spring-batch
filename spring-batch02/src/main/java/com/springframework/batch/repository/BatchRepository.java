package com.springframework.batch.repository;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.springframework.batch.models.Employees;

@Repository
public class BatchRepository {

	@Autowired
	private SqlSession sqlSession;
	
	public List<Employees> select_employees() {
		return sqlSession.selectList("batch.select_employees");
	}
}
