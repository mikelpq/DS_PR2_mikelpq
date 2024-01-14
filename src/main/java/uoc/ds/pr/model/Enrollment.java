package uoc.ds.pr.model;

import uoc.ds.pr.CTTCompaniesJobs;

import java.util.Comparator;

public class Enrollment {

    public static final Comparator<Enrollment> CMP_W_Q = Comparator.comparing(Enrollment::getWorkerWorkingDays);
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

    private int getWorkerWorkingDays(){
        int workingDays = 0;
        if (this.worker != null) {
            workingDays = getWorker().getWorkingDays();
        }
        return workingDays;
    }
}
