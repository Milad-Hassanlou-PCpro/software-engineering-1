package ap.exercises.finalproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManager implements Serializable {
    private final List<Employee> employees;

    public EmployeeManager() {
        this.employees = new ArrayList<>();
    }

    public void registerEmployee(String name, String employeeId, String username, String password) {
        if (isUsernameTaken(username)) {
            System.out.println("This username already exists. Please choose a different username.");
        } else {
            Employee newEmployee = new Employee(name, employeeId, username, password);
            employees.add(newEmployee);
            System.out.println("Employee registration completed successfully.");
        }
    }

    public Employee authenticateEmployee(String username, String password) {
        return employees.stream()
                .filter(e -> e.getUsername().equals(username) && e.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public void employeePasswordChanging(Employee employee, String password) {
        employees.stream()
                .filter(e->e==employee)
                .forEach(e->e.setPassword(password));
        System.out.println("Password Successfully Changed.");
    }

    private boolean isUsernameTaken(String username){
        return employees.stream()
                .anyMatch(e->e.getUsername().equals(username));
    }

    public int getEmployeesCount(){
        return employees.size();
    }
}
