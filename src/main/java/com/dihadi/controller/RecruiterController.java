package com.dihadi.controller;

import java.util.List;

import com.dihadi.dao.RecruiterDao;
import com.dihadi.model.Recruiter;

public class RecruiterController {
    RecruiterDao dao = new RecruiterDao();

    public void addRecruiter(String firstName, String middleName, String lastName,
                             String gender, String mobileNumber, String alternateMobile,
                             String email, String alternateEmail, String companyName,
                             String businessType, String password) {
        Recruiter recruiter = new Recruiter(firstName, middleName, lastName,
                gender, mobileNumber, alternateMobile,
                email, alternateEmail, companyName,
                businessType, password);
        dao.saveRecruiter(recruiter);
    }

    public Recruiter getRecruiter(String mobileNumber) {
        return dao.getRecruiter(mobileNumber);
    }
    
    public Recruiter getRecruiterByEmailOrMobile(String identifier) {
        return dao.getRecruiterByEmailOrMobile(identifier);
    }

    public void updateRecruiter(String firstName, String middleName, String lastName,
                                String gender, String mobileNumber, String alternateMobile,
                                String email, String alternateEmail, String companyName,
                                String businessType, String password) {
        Recruiter recruiter = new Recruiter(firstName, middleName, lastName,
                gender, mobileNumber, alternateMobile,
                email, alternateEmail, companyName,
                businessType, password);
        dao.updateRecruiter(recruiter);
    }

    public void deleteRecruiter(String mobileNumber) {
        dao.deleteRecruiter(mobileNumber);
    }

    public List<Recruiter> getAllRecruiters() {
        return dao.getAllRecruiters();
    }
}
