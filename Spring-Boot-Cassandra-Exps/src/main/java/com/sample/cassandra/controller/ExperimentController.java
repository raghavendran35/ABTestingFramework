package com.sample.cassandra.controller;

import com.datastax.driver.core.querybuilder.Update;
import com.datastax.driver.core.utils.UUIDs;
import com.sample.cassandra.entity.Experiment;
import com.sample.cassandra.repo.ExpRepo;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.Arrays;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.util.List;


@Controller
@RequestMapping(path = "/Experiments")
public class ExperimentController {

    private static Logger logger = LoggerFactory.getLogger(ExperimentController.class.getName());

    @Autowired
    private ExpRepo repository;


    //Getting all experiments
    //JSON formatting does not work for this!
    @GetMapping(path = "/allExperiments")
    public @ResponseBody String getAll(@RequestParam String userID) {
        logger.info("Experiment list is checking from cassandra");
        for (Experiment exp: this.repository.findAll()){
            this.repository.delete(exp);
            exp.setLastAccessedBy(userID);
            this.repository.save(exp);
        }
        return convertObject(this.repository.findAll());
    }




    //CREATing and experiment
    @GetMapping(path = "/addOneExperiment")
    public @ResponseBody String create(@RequestParam String name, @RequestParam String startDate,
                  @RequestParam String endDate, @RequestParam String description,
                  @RequestParam String status, @RequestParam int algId) {
        Experiment experiment = new Experiment(UUIDs.timeBased(), name, startDate, endDate, description,
                "none", "none", status, algId);
        logger.info("Experiment has been created and added.");
        this.repository.save(experiment);
        return "Saved\n";
    }


    //Finding a specific experiment
    @GetMapping(path = "/findOneExperiment")
    public @ResponseBody  String find(@RequestParam String expId, @RequestParam String userID) {
        logger.info("User delete process has started.");
        Experiment exp = this.repository.findById(UUID.fromString(expId));
        this.repository.delete(exp);
        exp.setLastAccessedBy(userID);
        this.repository.save(exp);
        return convertObject(exp);

    }


    //optional method
    //General Update method for an experiment
    //default will assign specialString, specialInt values, only kept to ease
    //having to deal with the parameter change
    //only one param at a time
    @GetMapping(path = "/updateOneExperiment")
    public @ResponseBody String update(@RequestParam String id, @RequestParam String toChange, @RequestParam int specialInt,
                       @RequestParam String specialString, @RequestParam String userID) {
        logger.info("User update process has started.");
        //would want to use findById normally, look into this!
        //not sure if below will work or have to be instantiated or created or something
        Experiment forChange = this.repository.findById(UUID.fromString(id));
        this.repository.delete(UUID.fromString(id));
        switch (toChange) {
            case "name":
                forChange.setName(specialString);
                break;
            case "startDate":
                forChange.setStartDate(specialString);
                break;
            case "endDate":
                forChange.setEndDate(specialString);
                break;
            case "description":
                forChange.setDescription(specialString);
                break;
            case "lastAccessedBy":
                forChange.setLastAccessedBy(specialString);
                break;
            case "lastEditedBy":
                forChange.setLastEditedBy(specialString);
                break;
            case "status":
                forChange.setStatus(specialString);
                break;
            case "algId":
                forChange.setAlgId(specialInt);
                break;
            default:
                System.out.println("failure");
                return null;
        }
        forChange.setLastAccessedBy(userID);
        this.repository.save(forChange);
        System.out.println("Experiment is updated and saved!\n");
        return convertObject(forChange);
    }



    //DELETE and Experiment operation

    @GetMapping(path = "/deleteOneExperiment")
    public @ResponseBody String delete(@RequestParam String expId) {
        logger.info("Experiment delete process has started.");
        this.repository.delete(UUID.fromString(expId));
        return "Experiment is deleted!\n";
    }


    //update and Change the status of an Experiment
    @GetMapping(path = "/changeExperimentStatus")
    public @ResponseBody String changeStatus(@RequestParam String expID, @RequestParam String newStatus, @RequestParam String userID){
        logger.info("Experiment status has been changed");
        Experiment currExperiment = this.repository.findById(UUID.fromString(expID));
        this.repository.delete(UUID.fromString(expID));
        currExperiment.setStatus(newStatus);
        currExperiment.setLastEditedBy(userID);
        this.repository.save(currExperiment);
        return "Experiment Status has been changed\n";
        }

    //make a copy of a specific experiment
    @GetMapping(path = "/makeCopy")
    public @ResponseBody String makeCopy(@RequestParam String expID){
        logger.info("Experiment status has been changed");
        Experiment currExperiment = this.repository.findById(UUID.fromString(expID));
        currExperiment.setId(com.datastax.driver.core.utils.UUIDs.timeBased());
        currExperiment.setLastAccessedBy("none");
        currExperiment.setLastEditedBy("none");
        this.repository.save(currExperiment);
        return "Copy of Experiment has been saved\n";

    }

    //Delete all Experiments
    @GetMapping(path = "/deleteAll")
    public @ResponseBody String deleteAll(@RequestParam String userID){
        this.repository.deleteAll();
        return "All current experiments have been deleted.\n";
    }

    //Find the Id, Name, status of all current Experiments, modify lastAccessedBy
    //for all
    @GetMapping(path = "/allStatus")
    public @ResponseBody String allStatus(@RequestParam String userID){
        ArrayList<String> allStats = new ArrayList<>();
        ArrayList<Experiment> allExps = (ArrayList<Experiment>) this.repository.findAll();
        for (Experiment exp: allExps){
            allStats.add("Id = "  + exp.getId().toString() + "-- Experiment Name : " + exp.getName() + "-- Experiment Status : " + exp.getStatus());
            this.repository.delete(exp);
            exp.setLastAccessedBy(userID);
            this.repository.save(exp);
        }

        //return Arrays.asList(allStats.keySet().toArray());
        return convertObject(allStats);
    }


    //change the EndDate
    @GetMapping(path = "/changeEndDate")
    public @ResponseBody String changeEndDate(@RequestParam String expID, @RequestParam String newEndDate,
                                              @RequestParam String userID){
        Experiment currExperiment = this.repository.findById(UUID.fromString(expID));
        this.repository.delete(UUID.fromString(expID));
        currExperiment.setEndDate(newEndDate);
        currExperiment.setLastEditedBy(userID);
        this.repository.save(currExperiment);
        System.out.println("Experiment's End Date has changed\n");
        return convertObject(currExperiment);
    }


    private static String convertObject(Object obj){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(obj);
    }


}

