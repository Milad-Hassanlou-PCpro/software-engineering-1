package ap.exercises.finalproject_ap;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// Persistence.java
// Stores the whole library state in an SQLite database file (library.db)
// through the SQLite JDBC driver (lib/sqlite-jdbc-3.45.1.0.jar).
// save() rewrites all tables inside one transaction; load() reads the
// tables back and rebuilds the object graph.
public class Persistence {

    private static final String DB_PATH = "library.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    public void save(LibrarySystem librarySystem) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            try {
                createTables(conn);
                clearAll(conn);

                for (Employee employee : librarySystem.getEmployeeManager().getEmployees()) {
                    insertEmployee(conn, employee);
                }
                for (Student student : librarySystem.getStudentManager().getStudents()) {
                    insertStudent(conn, student);
                }
                for (Book book : librarySystem.getBookManager().getBooks()) {
                    insertBook(conn, book);
                }
                for (LoanBook loan : librarySystem.getLoanBookManager().getLoans()) {
                    insertLoan(conn, loan);
                }
                for (LoanRequest request : librarySystem.getLoanRequestManager().getRequests()) {
                    insertRequest(conn, request);
                }
                for (Student student : librarySystem.getStudentManager().getBlackList()) {
                    insertBlacklistedStudent(conn, student.getUserId());
                }

                conn.commit();
                System.out.println("Saving completed.");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Saving failed!");
            e.printStackTrace();
        }
    }

    public LibrarySystem load() {
        if (!new File(DB_PATH).exists()) {
            return new LibrarySystem();
        }

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            createTables(conn);

            StudentManager studentManager = new StudentManager();
            EmployeeManager employeeManager = new EmployeeManager();
            LoanBookManager loanBookManager = new LoanBookManager();
            BookManager bookManager = new BookManager(loanBookManager);
            LoanRequestManager loanRequestManager = new LoanRequestManager();

            Map<String, Student> studentsById = new HashMap<>();
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT student_id, name, username, password FROM students")) {
                while (rs.next()) {
                    Student student = new Student(rs.getString("name"), rs.getString("student_id"),
                            rs.getString("username"), rs.getString("password"));
                    studentManager.loadStudent(student);
                    studentsById.put(student.getUserId(), student);
                }
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT student_id FROM blacklist")) {
                while (rs.next()) {
                    Student student = studentsById.get(rs.getString("student_id"));
                    if (student != null) {
                        studentManager.loadBlacklistedStudent(student);
                    }
                }
            }

            Map<String, Employee> employeesById = new HashMap<>();
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT employee_id, name, username, password FROM employees")) {
                while (rs.next()) {
                    Employee employee = new Employee(rs.getString("name"), rs.getString("employee_id"),
                            rs.getString("username"), rs.getString("password"));
                    employeeManager.loadEmployee(employee);
                    employeesById.put(employee.getUserId(), employee);
                }
            }

            Map<String, Book> booksById = new HashMap<>();
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT book_id, title, author, publish_year, registerer_id FROM books")) {
                while (rs.next()) {
                    Employee registerer = employeesById.get(rs.getString("registerer_id"));
                    Book book = new Book(registerer, rs.getString("title"), rs.getString("author"),
                            rs.getInt("publish_year"), rs.getString("book_id"));
                    bookManager.loadBook(book);
                    booksById.put(book.getBookId(), book);
                }
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT book_id, student_id, giver_id, getter_id, start_date, end_date, return_date FROM loans")) {
                while (rs.next()) {
                    Student student = studentsById.get(rs.getString("student_id"));
                    Book book = booksById.get(rs.getString("book_id"));
                    Employee giver = employeesById.get(rs.getString("giver_id"));
                    String getterId = rs.getString("getter_id");
                    Employee getter = getterId == null ? null : employeesById.get(getterId);
                    if (student == null || book == null || giver == null) {
                        continue;
                    }
                    LocalDate returnDate = rs.getString("return_date") == null
                            ? null : LocalDate.parse(rs.getString("return_date"));
                    LoanBook loan = new LoanBook(student, book, giver, getter,
                            LocalDate.parse(rs.getString("start_date")),
                            LocalDate.parse(rs.getString("end_date")),
                            returnDate);
                    loanBookManager.loadLoan(loan);
                }
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT book_id, student_id, start_date, end_date FROM loan_requests")) {
                while (rs.next()) {
                    Student student = studentsById.get(rs.getString("student_id"));
                    Book book = booksById.get(rs.getString("book_id"));
                    if (student == null || book == null) {
                        continue;
                    }
                    LoanRequest request = new LoanRequest(student, book,
                            LocalDate.parse(rs.getString("start_date")),
                            LocalDate.parse(rs.getString("end_date")));
                    loanRequestManager.loadRequest(request);
                }
            }

            System.out.println("Loading completed.");
            return new LibrarySystem(studentManager, employeeManager, bookManager,
                    loanBookManager, loanRequestManager);
        } catch (Exception e) {
            System.out.println("Loading saved data failed. A new empty library will be started.");
            e.printStackTrace();
            return new LibrarySystem();
        }
    }

    private void createTables(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS students ("
                    + "student_id TEXT PRIMARY KEY,"
                    + "name TEXT NOT NULL,"
                    + "username TEXT NOT NULL,"
                    + "password TEXT NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS employees ("
                    + "employee_id TEXT PRIMARY KEY,"
                    + "name TEXT NOT NULL,"
                    + "username TEXT NOT NULL,"
                    + "password TEXT NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS books ("
                    + "book_id TEXT PRIMARY KEY,"
                    + "title TEXT NOT NULL,"
                    + "author TEXT NOT NULL,"
                    + "publish_year INTEGER NOT NULL,"
                    + "registerer_id TEXT)");
            st.execute("CREATE TABLE IF NOT EXISTS loans ("
                    + "book_id TEXT NOT NULL,"
                    + "student_id TEXT NOT NULL,"
                    + "giver_id TEXT NOT NULL,"
                    + "getter_id TEXT,"
                    + "start_date TEXT NOT NULL,"
                    + "end_date TEXT NOT NULL,"
                    + "return_date TEXT)");
            st.execute("CREATE TABLE IF NOT EXISTS loan_requests ("
                    + "book_id TEXT NOT NULL,"
                    + "student_id TEXT NOT NULL,"
                    + "start_date TEXT NOT NULL,"
                    + "end_date TEXT NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS blacklist ("
                    + "student_id TEXT PRIMARY KEY)");
        }
    }

    private void clearAll(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("DELETE FROM blacklist");
            st.execute("DELETE FROM loan_requests");
            st.execute("DELETE FROM loans");
            st.execute("DELETE FROM books");
            st.execute("DELETE FROM students");
            st.execute("DELETE FROM employees");
        }
    }

    private void insertEmployee(Connection conn, Employee employee) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT OR REPLACE INTO employees (employee_id, name, username, password) VALUES (?,?,?,?)")) {
            ps.setString(1, employee.getUserId());
            ps.setString(2, employee.getName());
            ps.setString(3, employee.getUsername());
            ps.setString(4, employee.getPassword());
            ps.executeUpdate();
        }
    }

    private void insertStudent(Connection conn, Student student) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT OR REPLACE INTO students (student_id, name, username, password) VALUES (?,?,?,?)")) {
            ps.setString(1, student.getUserId());
            ps.setString(2, student.getName());
            ps.setString(3, student.getUsername());
            ps.setString(4, student.getPassword());
            ps.executeUpdate();
        }
    }

    private void insertBook(Connection conn, Book book) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT OR REPLACE INTO books (book_id, title, author, publish_year, registerer_id) VALUES (?,?,?,?,?)")) {
            ps.setString(1, book.getBookId());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setInt(4, book.getPublishYear());
            ps.setString(5, book.getBookRegisterer() == null ? null : book.getBookRegisterer().getUserId());
            ps.executeUpdate();
        }
    }

    private void insertLoan(Connection conn, LoanBook loan) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO loans (book_id, student_id, giver_id, getter_id, start_date, end_date, return_date) VALUES (?,?,?,?,?,?,?)")) {
            ps.setString(1, loan.getBook().getBookId());
            ps.setString(2, loan.getStudent().getUserId());
            ps.setString(3, loan.getGiverEmployee().getUserId());
            ps.setString(4, loan.getGetterEmployee() == null ? null : loan.getGetterEmployee().getUserId());
            ps.setString(5, loan.getStartDate().toString());
            ps.setString(6, loan.getEndDate().toString());
            ps.setString(7, loan.getReturnDate() == null ? null : loan.getReturnDate().toString());
            ps.executeUpdate();
        }
    }

    private void insertRequest(Connection conn, LoanRequest request) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO loan_requests (book_id, student_id, start_date, end_date) VALUES (?,?,?,?)")) {
            ps.setString(1, request.getBook().getBookId());
            ps.setString(2, request.getStudent().getUserId());
            ps.setString(3, request.getStartDate().toString());
            ps.setString(4, request.getEndDate().toString());
            ps.executeUpdate();
        }
    }

    private void insertBlacklistedStudent(Connection conn, String studentId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT OR REPLACE INTO blacklist (student_id) VALUES (?)")) {
            ps.setString(1, studentId);
            ps.executeUpdate();
        }
    }
}
