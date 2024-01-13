package uoc.ds.pr.util;

import uoc.ds.pr.CTTCompaniesJobs;
import uoc.ds.pr.CTTCompaniesJobsPR2;

public class LevelHelper {

    public static CTTCompaniesJobsPR2.Level getLevel(int hours){
        if (hours < 10) {
            return CTTCompaniesJobsPR2.Level.BEFINNER;
        } else if (hours < 200) {
            return CTTCompaniesJobsPR2.Level.INTERN;
        } else if (hours < 500) {
            return CTTCompaniesJobsPR2.Level.JUNIOR;
        } else if (hours < 1000) {
            return CTTCompaniesJobsPR2.Level.SENIOR;
        } else {
            return CTTCompaniesJobsPR2.Level.EXPERT;
        }
    }
}
