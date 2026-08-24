package com.dihadi.controller;

import java.util.List;

import com.dihadi.dao.WorkerDao;
import com.dihadi.model.Worker;

public class WorkerController {
    WorkerDao dao = new WorkerDao();

    public void addWorker(String firstName, String middleName, String lastName,
                          String mobileNumber, String alternateMobile, String email,
                          String gender, String dateOfBirth, String education,
                          String experience, int dailyWage) {
        Worker worker = new Worker(firstName, middleName, lastName,
                mobileNumber, alternateMobile, email,
                gender, dateOfBirth, education,
                experience, dailyWage);
        dao.saveWorker(worker);
    }

    public Worker getWorker(String mobileNumber) {
        return dao.getWorker(mobileNumber);
    }

    public void updateWorker(String firstName, String middleName, String lastName,
                             String mobileNumber, String alternateMobile, String email,
                             String gender, String dateOfBirth, String education,
                             String experience, int dailyWage) {
        Worker worker = new Worker(firstName, middleName, lastName,
                mobileNumber, alternateMobile, email,
                gender, dateOfBirth, education,
                experience, dailyWage);
        dao.updateWorker(worker);
    }

    public void deleteWorker(String mobileNumber) {
        dao.deleteWorker(mobileNumber);
    }

    public List<Worker> getAllWorkers() {
        return dao.getAllWorkers();
    }
}
