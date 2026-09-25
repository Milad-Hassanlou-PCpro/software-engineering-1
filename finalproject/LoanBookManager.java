package ap.exercises.finalproject;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoanBookManager implements Serializable {
    private List<LoanBook> loans;

    public LoanBookManager() {
        loans = new ArrayList<>();
    }

    public void addLoan(Employee giverEmployee, LoanRequest request) {
        LoanBook newLoanBook = new LoanBook(request, giverEmployee);
        loans.add(newLoanBook);
        System.out.println("Request successfully accepted. The book is ready for borrowing.");
    }

    public boolean isABookAvailable(Book targetBook) {
        long count = loans.stream()
                .filter(l -> l.getBook() == targetBook)
                .filter(l -> !(l.isReturned()))
                .count();
        return count == 0;
    }

    public void displayStudentStatistics(Student student) {
        int allLoansCount = 0;
        int notReturnedLoansCount = 0;
        int withDelayLoansCount = 0;
        for (LoanBook loanBook : loans) {
            if (loanBook.getStudent() == student) {
                allLoansCount++;
                if (!loanBook.isReturned()) {
                    notReturnedLoansCount++;
                } else {
                    if (loanBook.isDelayedInReturning()) {
                        withDelayLoansCount++;
                    }
                }
            }
        }

        System.out.print("All Borrowed Books: " + allLoansCount + "  ");
        System.out.print("Unreturned Books: " + notReturnedLoansCount + "  ");
        System.out.println("Overdue Books: " + withDelayLoansCount);
        System.out.println();

    }

    public int getLoanHistoryCount() {
        return loans.size();
    }

    public void returnBook(String bookId, Employee currentEmployee) {
        LoanBook loanBook = loans.stream()
                .filter(l -> l.getBook().getBookId().equals(bookId))
                .filter(l -> !(l.isReturned()))
                .findFirst()
                .orElse(null);
        if (loanBook == null) {
            System.out.println("There's not such a book on loan.");
            return;
        }
        loanBook.setGetterEmployee(currentEmployee);
        loanBook.setReturnDate(LocalDate.now());
        System.out.println("Book returned successfully.");
    }

    public Integer bookGivenByEmployeeCount(String employeeId) {
        return (int) loans.stream()
                .filter(l -> l.getGiverEmployee().getUserId().equals(employeeId))
                .count();
    }

    public Integer bookGottenByEmployeeCount(String employeeId) {
        return (int) loans.stream()
                .filter(l -> l.getGetterEmployee().getUserId().equals(employeeId))
                .count();
    }

    public int allLoansForBook(String bookId) {
        return (int) loans.stream()
                .filter(l -> l.getBook().getBookId().equals(bookId))
                .count();
    }

    public double averageBorrowingDuration(String bookId) {

        return loans.stream()
                .filter(l -> l.getBook().getBookId().equals(bookId) && l.isReturned())
                .mapToInt(l -> l.loanDuration())
                .average()
                .orElse(0);
    }

    public int allLoansCount() {
        return loans.size();
    }

    public int allUnreturnedLoansCount() {
        return (int) loans.stream()
                .filter(l -> !(l.isReturned()))
                .count();
    }

    public int allOverdueLoansCount() {
        return (int) loans.stream()
                .filter(l -> l.isDelayedInReturning())
                .count();
    }

    public void displayTenStudentsWithMostDelay() {
        Map<Student, Integer> delayMap = new HashMap<>();

        for (LoanBook loan : loans) {
            if (loan.isDelayedInReturning() && loan.delayDuration() > 0) {
                delayMap.merge(
                        loan.getStudent(),
                        loan.delayDuration(),
                        Integer::sum
                );
            }
        }

        if (delayMap.isEmpty()) {
            System.out.println("No students with delay.");
            return;
        }

        delayMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .forEachOrdered(e -> System.out.println(e.getKey() + " | Delay Duration: " + e.getValue()));
    }
}
