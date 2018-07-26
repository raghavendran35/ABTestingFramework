package com.hivedata.euclid.hilbert;

import com.hivedata.euclid.hilbert.entity.UserManagement;
import com.hivedata.euclid.hilbert.repo.UserManagementRepo;
import com.hivedata.euclid.hilbert.repo.AnalyticsRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {


    @Autowired
    private UserManagementRepo userRepo;

    @Autowired
    private AnalyticsRepo analyticsRepo;


//s    @Override
//    public void run(String... args) throws Exception {
//        // Example data for first insert. It can be deleted as soon as things go well
//        this.repository.save(new Experiment(UUIDs.timeBased(), "firstOne", "06.14.2018","06.14.2018", 3));
//    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
