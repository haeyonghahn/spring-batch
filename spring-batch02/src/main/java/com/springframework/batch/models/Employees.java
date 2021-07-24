package com.springframework.batch.models;

public class Employees {

	private int emp_no;
	private String first_name;
	
	public int getEmp_no() {
		return emp_no;
	}
	public void setEmp_no(int emp_no) {
		this.emp_no = emp_no;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	
	@Override
	public String toString() {
		return "Employees [emp_no=" + emp_no + ", first_name=" + first_name + "]";
	}
}
