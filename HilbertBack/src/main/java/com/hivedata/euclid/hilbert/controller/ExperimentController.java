package com.hivedata.euclid.hilbert.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.hivedata.euclid.hilbert.entity.Experiment;
import com.hivedata.euclid.hilbert.repo.ExpRepo;
import com.hivedata.euclid.hilbert.repo.BucketRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hivedata.euclid.hilbert.controller.date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.UUID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <h1>Controller for Experiment Management</h1>
 * The ExperimentController program handles the elementary CRUD operations for the experiments Management of Hilbert.
 * @author Sachin Raghavendran
 * @version 1.0
 * @since 2018-06-25
 */

public class ExperimentController {


    /**
     * This method returns all of the current experiments along with their respective characteristics. The lastAccessedBy
     * parameter of each experiment will be updated to correspond to the current user. The user validity will be checked
     * in the business logic class. This method does not throw any exception (it assumes the user is a valid user).
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @param repository This corresponds to the ExpRepo that the experiment is pulled from.
     * @return This returns a JSON String representation of all the buckets.
     */
    public static String getAll(String userID, ExpRepo repository) {
        for (Experiment exp: repository.findAll()){
            repository.delete(exp);
            repository.save(exp);
        }
        return convertObject(repository.findAll());
    }

    


    /**
     * This method is the pivotal Experiment creation method. Errors will be thrown if the required parameters
     * in the request call are not called and if the start and end dates are not valid.
     * @param name This String corresponds to the name of the experiment.
     * @param startDate This String corresponds to the start date of the experiment. Has to be of the following formats: "mm-dd" or "mm-dd-yyyy".
     * @param endDate This String corresponds to the end date of the experiment. Has to be of the following formats: "mm-dd" or "mm-dd-yyyy".
     * @param description This String corresponds to a short description of the experiment.
     * @param status This String corresponds to the status of the experiment. Default should be something tending to "notStarted".
     * @param algId This String corresponds to the selection routine for the buckets of the experiment.
     * @param repository This corresponds to the ExpRepo that the experiment is pulled from.
     * @return This returns a string establishing whether the experiment has been successfully saved.
     */
    public static String create(String name, String startDate, String endDate, String description,
                  String status, int algId, ExpRepo repository) {
        if (!date.validateDates(startDate, endDate)){
            return "This is not a valid experiment! \n";
        }
        Experiment experiment = new Experiment(UUIDs.timeBased(), name, startDate, endDate, description,
         "none", status, algId);
        repository.save(experiment);
        return "Experiment Saved\n";
    }


    //Finding a specific experiment
    public static String find(String expID, String userID, ExpRepo repository) {
        Experiment exp = findByID(expID, repository);
        return convertObject(exp);
    }


    /**
     * This method finds a specific experiment based on its respective id, and then deletes it. It does not catch an exception
     * if the experiment is not found (v 1.0). However, any behavior is recorded in the logger and the request call will not be
     * successful if the parameter is not valid.
     * @param expID The String (that will be converted to UUID) identification of an experiment.
     * @param repository This corresponds to the ExpRepo that the experiment is pulled from.
     * @return This returns a string that establishes whether the experiment is deleted.
     */
    public static String delete(String expID, ExpRepo repository) {
        repository.delete(UUID.fromString(expID));
        return "Experiment is deleted!\n";
    }


    /**
     * This method adds a specific bucket based on its respective id. However, any behavior is recorded in the logger and the request call will not be
     * successful if the parameter is not valid.
     * @param expID The String (that will be converted to UUID) identification of an experiment.
     * @param repository This corresponds to the ExpRepo that the experiment is pulled from.
     * @return This returns a string that establishes whether the experiment is added.
     */
    public static String addBucket(String expID, String bucketID, ExpRepo repository) {
        Experiment exp = findByID(expID, repository);
        repository.delete(exp);
        exp.addBucket(bucketID);
        repository.save(exp);
        return "Bucket is added!\n";
    }


    /**
     * This method deletes a specific bucket based on its respective id. However, any behavior is recorded in the logger and the request call will not be
     * successful if the parameter is not valid.
     * @param expID The String (that will be converted to UUID) identification of an experiment.
     * @param repository This corresponds to the ExpRepo that the experiment is pulled from.
     * @return This returns a string that establishes whether the experiment is deleted.
     */
    public static String deleteBucket(String expID, String bucketID, ExpRepo repository) {
        Experiment exp = findByID(expID, repository);
        repository.delete(exp);
        exp.getBuckets().remove(bucketID);
        repository.save(exp);
        return "Bucket is deleted!\n";
    }



    /**
     * This method modifies the status of a specific experiment (identified by the String-to-UUID expID).
     * Also, the lastEditedBy param is modified to be the userID. If the bucket is not found (or does not exist),
     * the request call will not be performed.
     * @param expID The String (that will be converted to UUID) identification of a experiment.
     * @param newStatus This String corresponds to the new status of the experiment.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @param repository This corresponds to the ExpRepo that the experiment is pulled from.
     * @return This returns a JSON String representation of the updated experiment.
     */
    public static String changeStatus(String expID, String newStatus,
                                             String userID, ExpRepo repository){
        Experiment currExperiment = findByID(expID, repository);
        repository.delete(UUID.fromString(expID));
        currExperiment.setStatus(newStatus);
        currExperiment.setLastEditedBy(userID);
        repository.save(currExperiment);
        return convertObject(currExperiment);
        }


    /**
     * This method modifies the selection routine of a specific experiment (identified by the String-to-UUID expID).
     * Also, the lastEditedBy param is modified to be the userID. If the bucket is not found (or does not exist),
     * the request call will not be performed.
     * @param expID The String (that will be converted to UUID) identification of a experiment.
     * @param algID This String corresponds to the new selection routine for the experiment.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @param repository This corresponds to the ExpRepo that the experiment is pulled from.
     * @return This returns a JSON String representation of the updated experiment.
     */
    public static String changeRoutine(String expID, int algID,
                                      String userID, ExpRepo repository){
        Experiment currExperiment = findByID(expID, repository);
        repository.delete(UUID.fromString(expID));
        currExperiment.setAlgId(algID);
        currExperiment.setLastEditedBy(userID);
        repository.save(currExperiment);
        return convertObject(currExperiment);
    }


    /**
     * This method is the all-powerful clear method for all of the experiments in the database. The Business Logic class
     * will validate the person who is performing this sudo action. Errors do not occur for this method (as of v 1.0).
     * @param repository This corresponds to the ExpRepo that the experiment is pulled from.
     * @return This method returns a string that displays whether all the users have been removed form the database.
     */

    public static String deleteAll(ExpRepo repository){
        repository.deleteAll();
        return "All current experiments have been deleted.\n";
    }


    /**
     * This method modifies the end date of a specific experiment (identified by the String-to-UUID expID).
     * Also, the lastEditedBy param is modified to be the userID. If the bucket is not found (or does not exist),
     * the request call will not be performed.
     * @param expID The String (that will be converted to UUID) identification of a experiment.
     * @param newEndDate This String corresponds to the new end date of the experiment.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @param repository This corresponds to the ExpRepo that the experiment is pulled from.
     * @return This returns a JSON String representation of the updated experiment.
     */
    public static String changeEndDate(String expID, String newEndDate, String userID, ExpRepo repository){
        Experiment currExperiment = findByID(expID, repository);
        repository.delete(UUID.fromString(expID));
        currExperiment.setEndDate(newEndDate);
        currExperiment.setLastEditedBy(userID);
        repository.save(currExperiment);
        System.out.println("Experiment's End Date has changed\n");
        return convertObject(currExperiment);
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                      UTILITY AND OTHER METHODS                                    
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This method converts and POJO to a JSON String using Google's GSON libraries. This method throws a null pointer
     * exception should the object be null.
     * @param obj A POJO.
     * @return This method returns a JSON string representation of the POJO.
     */

    private static String convertObject(Object obj){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(obj);
    }

    /**
    * This method is a utility method for the Experiment Controller class that essentially
    * finds a Experiment by ID from a given ExpRepo. An error is thrown with illegitimate parameters
    * and/or if the experiment is not found in the repository.
    * @param expID The String (that will be converted to UUID) identification of an experiment.
    * @param repository This corresponds to the userRepo that the user is pulled from.
    */
    private static Experiment findByID(String expID, ExpRepo repository){
        for (Experiment exp: repository.findAll()){
            if (exp.getId().equals(UUID.fromString(expID))){
                return exp;
            }
        }
        return null;
    }


    /**
     * This method is a utility method that lists out all the experimentIDs for the experiments in an
     * experiment repository. An error will only be thrown if the repository is not found.
     * @param repository This corresponds to the ExpRepo that the experiment is pulled from.
     * @return This returns an arraylist of all of the Experiment IDs the ExpRepo repository.
     */

    public static ArrayList<UUID> getAllExpIDs(ExpRepo repository){
        ArrayList<UUID> result = new ArrayList<>();
        for (Experiment exp: repository.findAll()){
            result.add(exp.getId());
        }
        return result;
    }


    /**
     * This method makes a copy of the specific user, but the "new" user has their own unique ID. This is a utility method.
     * Nevertheless, behavior is recorded in the logger and the request call will not be successful should there be erratic input.
     * @param expID The String (that will be converted to UUID) identification of an experiment.
     * @return This method returns a JSON String representation of the new experiment copy.
     */
    /*
    @PostMapping(path = "/makeCopy")
    public @ResponseBody String makeCopy(@RequestParam String expID){
        logger.info("Experiment status has been changed");
        Experiment currExperiment = this.repository.findById(UUID.fromString(expID));
        currExperiment.setId(com.datastax.driver.core.utils.UUIDs.timeBased());
        currExperiment.setLastAccessedBy("none");
        currExperiment.setLastEditedBy("none");
        this.repository.save(currExperiment);
        return "Copy of Experiment has been saved\n";

    }*/

        //optional method
    //General Update method for an experiment
    //default will assign specialString, specialInt values, only kept to ease
    //having to deal with the parameter change
    //only one param at a time
    //@PutMapping(path = "/updateOneExperiment")
    //public @ResponseBody String update(@RequestParam String id, @RequestParam String toChange, @RequestParam int specialInt,
    /*                   @RequestParam String specialString, @RequestParam String userID) {
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
    }*/


}

