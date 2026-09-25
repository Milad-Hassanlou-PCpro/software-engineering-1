package ap.exercises.finalproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StudentManager implements Serializable {
    private List<Student> students;
    private final List<Student> blackList;

    public StudentManager() {
        this.students = new ArrayList<>();
        this.blackList = new ArrayList<>();
    }

    public void registerStudent(String name, String studentId, String username, String password) {
        if (isUsernameTaken(username)) {
            System.out.println("This username already exists. Please choose a different username.");
            return;
        }

        Student newStudent = new Student(name, studentId, username, password);
        students.add(newStudent);
        System.out.println("Student registration completed successfully.");
    }

    public Student authenticateStudent(String username, String password) {
        return students.stream()
                .filter(s -> s.getUsername().equals(username) && s.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public void editStudentInformation(Student student, String username, String password) {
        if (isUsernameTaken(username)) {
            System.out.println("This username already exists. Please choose a different username.");
            return;
        }
        for (Student st : students) {
            if (st == student) {
                st.setUsername(username);
                st.setPassword(password);
                System.out.println("Username and Password Successfully Changed.");
                break;
            }
        }
    }

    public void displayStudents() {
        System.out.println("\n--- List of Registered Students ---");

        if (students.isEmpty()) {
            System.out.println("No students have registered yet.");
            return;
        }

        for (Student student : students) {
            System.out.println(student);
        }
    }

    public Student searchById(String id) {
        return students.stream()
                .filter(s -> s.getUserId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void addStudentToBlackList(String id) {
        Student student = searchById(id);
        if (student == null) {
            System.out.println("There's no student with this id.");
            return;
        }
        System.out.println("Student-> " + student + " will be blocked.");
        blackList.add(student);
        System.out.println("Blocking done.");
    }

    public void removeStudentFromBlackList(String id) {
        Student student = searchById(id);
        if (student == null) {
            System.out.println("There's no student with this id.");
            return;
        }
        System.out.println("Student-> " + student + " will be unblocked.");
        blackList.remove(student);
        System.out.println("Unblocking done.");
    }

    public boolean isBlocked(Student student) {
        int count = (int) blackList.stream()
                .filter(s -> s == student)
                .count();

        return count != 0;
    }

    private boolean isUsernameTaken(String username) {
        return students.stream().anyMatch(s -> s.getUsername().equals(username));
    }

    public int getStudentCount() {
        return students.size();
    }

    public void displayStudentStatistics(LoanBookManager loanBookManager) {
        if (students.size() == 0) {
            System.out.println("There's not a student in list.");
            return;
        }
        students.stream()
                .forEach(s -> {
                    System.out.println(s + " :::");
                    loanBookManager.displayStudentStatistics(s);
                });
    }
}