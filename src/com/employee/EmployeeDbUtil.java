package com.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class EmployeeDbUtil {

	private static EmployeeDbUtil instance;
	private DataSource dataSource;
	private String jndiName = "java:comp/env/jdbc/employee_tracker";
	
	public static EmployeeDbUtil getInstance() throws Exception {
		if (instance == null) {
			instance = new EmployeeDbUtil();
		}
		
		return instance;
	}
	
	private EmployeeDbUtil() throws Exception {		
		dataSource = getDataSource();
	}

	private DataSource getDataSource() throws NamingException {
		Context context = new InitialContext();
		
		DataSource theDataSource = (DataSource) context.lookup(jndiName);
		
		return theDataSource;
	}
		
	public List<Employee> getEmployees() throws Exception {

		List<Employee> employees = new ArrayList<>();

		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		
		try {
			myConn = getConnection();

			String sql = "select * from employees order by last_name";

			myStmt = myConn.createStatement();

			myRs = myStmt.executeQuery(sql);

			// process result set
			while (myRs.next()) {
				
				// retrieve data from result set row
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				String phoneNumber = myRs.getString("phone_number");
				String department = myRs.getString("department");

				// create new student object
				Employee tempEmployee = new Employee(id, firstName, lastName,
						email, phoneNumber, department);

				// add it to the list of students
				employees.add(tempEmployee);
			}
			
			return employees;		
		}
		finally {
			close (myConn, myStmt, myRs);
		}
	}

	public void addEmployee(Employee theEmployee) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			myConn = getConnection();

			String sql = "insert into employees (first_name, last_name, email, phone_number, department) values (?, ?, ?, ?, ?)";

			myStmt = myConn.prepareStatement(sql);

			// set params
			myStmt.setString(1, theEmployee.getFirstName());
			myStmt.setString(2, theEmployee.getLastName());
			myStmt.setString(3, theEmployee.getEmail());
			myStmt.setString(4, theEmployee.getPhoneNumber());
			myStmt.setString(5, theEmployee.getDepartment());
			
			myStmt.execute();			
		}
		finally {
			close (myConn, myStmt);
		}
		
	}
	
	public Employee getEmployee(int employeeId) throws Exception {
	
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		
		try {
			myConn = getConnection();

			String sql = "select * from employees where id=?";

			myStmt = myConn.prepareStatement(sql);
			
			// set params
			myStmt.setInt(1, employeeId);
			
			myRs = myStmt.executeQuery();

			Employee theEmployee = null;
			
			// retrieve data from result set row
			if (myRs.next()) {
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				String phoneNumber = myRs.getString("phone_number");
				String department = myRs.getString("department");

				theEmployee = new Employee(id, firstName, lastName,
						email, phoneNumber, department);
			}
			else {
				throw new Exception("Could not find employee id: " + employeeId);
			}

			return theEmployee;
		}
		finally {
			close (myConn, myStmt, myRs);
		}
	}
	
	public void updateEmployee(Employee theEmployee) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			myConn = getConnection();

			String sql = "update employees "
						+ " set first_name=?, last_name=?, email=?, phone_number=?, department=?"
						+ " where id=?";

			myStmt = myConn.prepareStatement(sql);

			// set params
			myStmt.setString(1, theEmployee.getFirstName());
			myStmt.setString(2, theEmployee.getLastName());
			myStmt.setString(3, theEmployee.getEmail());
			myStmt.setString(4, theEmployee.getPhoneNumber());
			myStmt.setString(5, theEmployee.getDepartment());
			myStmt.setInt(6, theEmployee.getId());
			
			
			myStmt.execute();
		}
		finally {
			close (myConn, myStmt);
		}
		
	}
	
	public void deleteEmployee(int employeeId) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;
		try {
			myConn = getConnection();

			String sql = "delete from employees where id=?";

			myStmt = myConn.prepareStatement(sql);

			// set params
			myStmt.setInt(1, employeeId);
			
			myStmt.execute();
		}
		finally {
			close (myConn, myStmt);
		}		
	}	
	
	private Connection getConnection() throws Exception {

		Connection theConn = dataSource.getConnection();
		
		return theConn;
	}
	
	private void close(Connection theConn, Statement theStmt) {
		close(theConn, theStmt, null);
	}
	
	private void close(Connection theConn, Statement theStmt, ResultSet theRs) {

		try {
			if (theRs != null) {
				theRs.close();
			}

			if (theStmt != null) {
				theStmt.close();
			}

			if (theConn != null) {
				theConn.close();
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}	
}
