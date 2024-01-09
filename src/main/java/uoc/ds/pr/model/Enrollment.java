package uoc.ds.pr.model;

import uoc.ds.pr.CTTCompaniesJobs;

public class Enrollment {
    private Worker worker;
    private JobOffer jobOffer;

    private CTTCompaniesJobs.Response response;

    public Enrollment(JobOffer jobOffer, Worker worker, CTTCompaniesJobs.Response response) {
        this.jobOffer = jobOffer;
        this.worker = worker;
        this.response = response;
    }

    public Worker getWorker() {
        return this.worker;
    }
}
