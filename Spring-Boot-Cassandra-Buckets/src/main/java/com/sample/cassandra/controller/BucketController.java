package com.sample.cassandra.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.sample.cassandra.entity.Bucket;
import com.sample.cassandra.repo.BucketRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

@Controller
@RequestMapping(path = "/Buckets")
public class BucketController {

    private static Logger logger = LoggerFactory.getLogger(BucketController.class.getName());

    @Autowired
    private BucketRepo repository;


    //READ1 OPERATION
    @GetMapping(path = "/allBuckets")
    public @ResponseBody String getAll(@RequestParam String userID) {
        logger.info("Bucket list is checking from cassandra");
        ArrayList<String> answers = new ArrayList<>();
        for (Bucket b: repository.findAll()){
            this.repository.delete(b);
            b.setLastAccessedBy(userID);
            this.repository.save(b);
        }

        return convertObject((ArrayList<Bucket>) this.repository.findAll());
    }

    //Model Change operation
    @GetMapping(path = "/changeModel")
    public @ResponseBody String changeModel(@RequestParam String bucketID, @RequestParam int newModel, @RequestParam String userID){
        Bucket buck = this.repository.findOne(UUID.fromString(bucketID));
        this.repository.delete(buck);
        buck.setModel(newModel);
        buck.setLastEditedBy(userID);
        this.repository.save(buck);
        System.out.println("Model has been changed for the Bucket\n");
        return convertObject(buck);
    }


    //CREATE OPERATION
    @GetMapping(path = "/addOneBucket")
    public @ResponseBody String create(@RequestParam String experimentID, @RequestParam String bucketName,
                   @RequestParam String description, @RequestParam double allocation,
                  @RequestParam int model, @RequestParam String startDate, @RequestParam String endDate,
                  @RequestParam String lastAccessedBy, @RequestParam String lastEditedBy, @RequestParam String status) {
        Bucket bucket = new Bucket(UUIDs.timeBased(), experimentID,  bucketName, description, allocation, model,
                startDate, endDate, lastAccessedBy, lastEditedBy, status);
        logger.info("Experiment has been created and added.");
        this.repository.save(bucket);
        return "Saved\n";
    }

    //find a specific bucket
    @GetMapping(path = "/findOneBucket")
    public @ResponseBody  String find(@RequestParam String bucketID, @RequestParam String userID) {
        logger.info("User delete process has started.");
        Bucket buck = this.repository.findOne(UUID.fromString(bucketID));
        buck.setLastAccessedBy(userID);
        return convertObject(buck);
    }


    //change bucket status

    @GetMapping(path = "/changeBucketStatus")
    public @ResponseBody String changeStatus(@RequestParam String bucketID, @RequestParam String newStatus, @RequestParam String userID) {
        logger.info("Bucket update process has started.");
        Bucket buck = this.repository.findOne(UUID.fromString(bucketID));
        this.repository.delete(buck.getBucketId());
        buck.setLastEditedBy(userID);
        buck.setStatus(newStatus);
        this.repository.save(buck);
        return "Bucket status has been changed \n";
    }



    //DELETE operation

    @GetMapping(path = "/deleteOneBucket")
    public @ResponseBody String delete(@RequestParam String bucketID) {
        logger.info("Bucket delete process has started.");
        this.repository.delete(UUID.fromString(bucketID));
        return "Bucket is deleted!\n";
    }


    //View all buckets for an experiment
    @GetMapping(path = "/bucketsExp")
    public @ResponseBody String bucketsExp(@RequestParam String expID, @RequestParam String userID){
        ArrayList<Bucket> buckets = new ArrayList<>();
        for (Bucket b: this.repository.findAll()){
            if (b.getExperimentId().equals(expID)){
                buckets.add(b);
                b.setLastAccessedBy(userID);
            }
        }
        return convertObject(buckets);
    }

    //make a copy of a specific bucket
    @GetMapping(path = "/bucketCopy")
    public @ResponseBody String bucketCopy(@RequestParam String bucketID){
        Bucket bucket = this.repository.findOne(UUID.fromString(bucketID));
        Bucket bucket1 = bucket;
        bucket1.setBucketId(UUIDs.timeBased());
        bucket1.setLastAccessedBy("none");
        bucket1.setLastEditedBy("none");
        this.repository.save(bucket1);
        return "A copy of your bucket has been made\n";
    }



    //validate a bucket for its size
    @GetMapping(path = "/validBucket")
    public @ResponseBody String validBucket(@RequestParam String expID){
        double totalBucketSize = 0;
        for (Bucket b: this.repository.findAll()){
            if (b.getExperimentId().equals(expID)){
                totalBucketSize+=b.getAllocation();
            }
        }
        return String.valueOf(totalBucketSize == 1.0 || totalBucketSize == 1) + "\n";
    }





    //change bucket allocation
    @GetMapping(path = "/changeAlloc")
    public @ResponseBody String changeAlloc(@RequestParam String bucketID,
                                            @RequestParam double newAlloc,
                                            @RequestParam String userID) {
        Bucket bucket = this.repository.findOne(UUID.fromString(bucketID));
        this.repository.delete(bucket);
        bucket.setLastEditedBy(userID);
        bucket.setAllocation(newAlloc);
        this.repository.save(bucket);
        return "Bucket size has been changed\n";
    }




    //delete all buckets

    @GetMapping(path = "/deleteAll")
    public @ResponseBody String deleteAll(){
        this.repository.deleteAll();
        return "All of the Buckets have been deleted\n";
    }

    private static String convertObject(Object obj){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(obj);
    }
}

