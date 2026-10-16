package ap.exercises.finalproject_ap;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LoanBook implements Serializable, Loanable {

    private static final long serialVersionUID = 1L;

    private Student student;
    private Book book;
    private Employee giverEmployee;
    private Employee getterEmployee;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate returnDate;


    public LoanBook(LoanRequest loanRequest, Employee giverEmployee) {
        this.student = loanRequest.getStudent();
        this.book = loanRequest.getBook();
        this.giverEmployee = giverEmployee;
        this.startDate = loanRequest.getStartDate();
        this.endDate = loanRequest.getEndDate();
    }

    // Full constructor used by Persistence when rebuilding from the database.
    public LoanBook(Student student, Book book, Employee giverEmployee, Employee getterEmployee,
                    LocalDate startDate, LocalDate endDate, LocalDate returnDate) {
        this.student = student;
        this.book = book;
        this.giverEmployee = giverEmployee;
        this.getterEmployee = getterEmployee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.returnDate = returnDate;
    }

    public void setGetterEmployee(Employee getterEmployee) {
        this.getterEmployee = getterEmployee;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Employee getGiverEmployee() {
        return giverEmployee;
    }

    public Employee getGetterEmployee() {
        return getterEmployee;
    }

    public Student getStudent() {
        return student;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    @Override
    public boolean isReturned() {
        return (returnDate != null || getterEmployee != null);
    }

    @Override
    public boolean isDelayedInReturning() {
        return (returnDate != null && returnDate.isAfter(endDate));
    }

    @Override
    public int loanDuration() {
        if (startDate == null || returnDate == null) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(startDate, returnDate);
    }

    @Override
    public int delayDuration() {
        if (endDate == null || returnDate == null) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(endDate, returnDate);
    }
}

