package ap.exercises.finalproject;

public interface Loanable {
    public boolean isReturned();
    public boolean isDelayedInReturning();
    public int loanDuration();
    public int delayDuration();
}
