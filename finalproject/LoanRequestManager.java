package ap.exercises.finalproject;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class LoanRequestManager implements Serializable {
    private Queue<LoanRequest> requests;

    public LoanRequestManager() {
        this.requests = new LinkedList<>();
    }

    public void addLoanRequest(Book book, Student student, LocalDate startDate, LocalDate endDate) {
        LoanRequest request = new LoanRequest(student, book, startDate, endDate);
        requests.add(request);
        System.out.println("The request for a book signed in system.");
        System.out.println("--The Request Information: ");
        System.out.println(request);
    }

    public List<LoanRequest> listOfRecentRequests() {
        renewList();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        return requests.stream()
                .filter(r -> r.getStartDate().isEqual(today) || r.getStartDate().isEqual(yesterday))
                .collect(Collectors.toList());
    }

    public int requestCountForBook(String bookId) {
        renewList();
        return (int) requests.stream()
                .filter(r -> r.getBook().getBookId().equals(bookId))
                .count();
    }

    public void deleteRequest(LoanRequest request) {
        requests.remove(request);
    }

    private void renewList() {
        //Delete old requests from list of them.

        LocalDate yesterday = LocalDate.now().minusDays(1);
        requests = requests.stream()
                .filter(r -> !(r.getStartDate().isBefore(yesterday)))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public int allCurrenRequestsCount() {
        renewList();
        return requests.size();
    }
}
