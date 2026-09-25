package ap.exercises.finalproject;

public class Employee extends PersonParent {

    public Employee(String name, String userId, String username, String password) {
        super(name, userId, username, password);
    }

    @Override
    public String toString() {
        return "Employee Name: " + super.getName() +
                " | Employee ID: " + super.getUserId() +
                " | Username: " + super.getUsername();
    }
}
