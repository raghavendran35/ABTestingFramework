package com.sample.cassandra;

import com.datastax.driver.core.utils.UUIDs;
import com.sample.cassandra.repo.BucketRepo;
import com.sample.cassandra.entity.Bucket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    @Autowired
    private BucketRepo repository;

//s    @Override
//    public void run(String... args) throws Exception {
//        // Example data for first insert. It can be deleted as soon as things go well
//        this.repository.save(new Experiment(UUIDs.timeBased(), "firstOne", "06.14.2018","06.14.2018", 3));
//    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
