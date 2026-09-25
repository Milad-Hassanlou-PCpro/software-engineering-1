package ap.exercises.finalproject;

public class Student extends PersonParent {


    public Student(String name, String studentId, String username, String password) {
        super(name, studentId, username, password);
    }

    @Override
    public String toString() {
        return "Student Name: " + super.getName() +
                " | Student ID : " + super.getUserId() +
                " | Username: " + super.getUsername();
    }
}
