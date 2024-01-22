package uoc.ds.pr.model;

public class Rating {
    private final JobOffer jobOffer;
    private final int value;
    private final String message;
    private final Worker worker;

    public Rating(JobOffer jobOffer, int value, String message, Worker worker) {
        this.jobOffer = jobOffer;
        this.value = value;
        this.message = message;
        this.worker = worker;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }
}
