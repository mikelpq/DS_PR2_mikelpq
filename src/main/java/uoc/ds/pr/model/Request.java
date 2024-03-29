package uoc.ds.pr.model;

import uoc.ds.pr.CTTCompaniesJobs;

import java.time.LocalDate;

public class Request {

    private final String requestId;
    private final String jobOfferId;
    private final Company company;
    private final String description;
    private final CTTCompaniesJobs.Qualification minQualification;
    private final int maxWorkers;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private CTTCompaniesJobs.Status status;
    private String descriptionStatus;
    private LocalDate dateStatus;


    public Request(String requestId, String jobOfferId, Company company, String description, CTTCompaniesJobs.Qualification minQualification,
                   int maxWorkers, LocalDate startDate, LocalDate endDate) {
        this.requestId = requestId;
        this.jobOfferId = jobOfferId;
        this.company = company;
        this.description = description;
        this.minQualification = minQualification;
        this.maxWorkers = maxWorkers;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = CTTCompaniesJobs.Status.PENDING;
    }

    public void update(CTTCompaniesJobs.Status status, LocalDate date, String message) {
        this.status = status;
        this.dateStatus = date;
        this.descriptionStatus = message;
    }

    public boolean isEnabled() {
        return this.status == CTTCompaniesJobs.Status.ENABLED;
    }

    public JobOffer getJobOffer() {
        return new JobOffer(this.jobOfferId, this, this.company, this.description,
                this.minQualification, this.maxWorkers, this.startDate, this.endDate );
    }

    public CTTCompaniesJobs.Status getStatus() {
        return status;
    }

    public LocalDate getDateStatus() {
        return dateStatus;
    }

    public String getDescriptionStatus() {
        return descriptionStatus;
    }
}
