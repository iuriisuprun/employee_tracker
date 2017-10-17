package com.employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class EmployeeController {

	private List<Employee> employees;
	private EmployeeDbUtil employeeDbUtil;
	private Logger logger = Logger.getLogger(getClass().getName());
	
	public EmployeeController() throws Exception {
		employees = new ArrayList<>();
		
		employeeDbUtil = EmployeeDbUtil.getInstance();
	}
	
	public List<Employee> getEmployees() {
		return employees;
	}

	public void loadEmployees() {

		logger.info("Loading employees");
		
		employees.clear();

		try {
			
			// get all employees from database
			employees = employeeDbUtil.getEmployees();
			
		} catch (Exception exc) {
			// send this to server logs
			logger.log(Level.SEVERE, "Error loading employees", exc);
			
			// add error message for JSF page
			addErrorMessage(exc);
		}
	}
		
	public String addEmployee(Employee theEmployee) {

		logger.info("Adding employee: " + theEmployee);

		try {
			
			// add employee to the database
			employeeDbUtil.addEmployee(theEmployee);
			
		} catch (Exception exc) {
			// send this to server logs
			logger.log(Level.SEVERE, "Error adding employees", exc);
			
			// add error message for JSF page
			addErrorMessage(exc);

			return null;
		}
		
		return "list-employees?faces-redirect=true";
	}

	public String loadEmployee(int employeeId) {
		
		logger.info("loading employee: " + employeeId);
		
		try {
			// get employee from database
			Employee theEmployee = employeeDbUtil.getEmployee(employeeId);
			
			// put in the request attribute ... so we can use it on the form page
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();		

			Map<String, Object> requestMap = externalContext.getRequestMap();
			requestMap.put("employee", theEmployee);	
			
		} catch (Exception exc) {
			// send this to server logs
			logger.log(Level.SEVERE, "Error loading employee id:" + employeeId, exc);
			
			// add error message for JSF page
			addErrorMessage(exc);
			
			return null;
		}
				
		return "update-employee-form.xhtml";
	}	
	
	public String updateEmployee(Employee theEmployee) {

		logger.info("updating employee: " + theEmployee);
		
		try {
			
			// update employee in the database
			employeeDbUtil.updateEmployee(theEmployee);
			
		} catch (Exception exc) {
			// send this to server logs
			logger.log(Level.SEVERE, "Error updating employee: " + theEmployee, exc);
			
			// add error message for JSF page
			addErrorMessage(exc);
			
			return null;
		}
		
		return "list-employees?faces-redirect=true";		
	}
	
	public String deleteEmployee(int employeeId) {

		logger.info("Deleting employee id: " + employeeId);
		
		try {

			// delete the employee from the database
			employeeDbUtil.deleteEmployee(employeeId);
			
		} catch (Exception exc) {
			// send this to server logs
			logger.log(Level.SEVERE, "Error deleting employee id: " + employeeId, exc);
			
			// add error message for JSF page
			addErrorMessage(exc);
			
			return null;
		}
		
		return "list-employees";	
	}	
	
	private void addErrorMessage(Exception exc) {
		FacesMessage message = new FacesMessage("Error: " + exc.getMessage());
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
}
