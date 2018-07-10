package com.hivedata.euclid.hilbert.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.hivedata.euclid.hilbert.entity.Bucket;
import com.hivedata.euclid.hilbert.entity.Experiment;
import com.hivedata.euclid.hilbert.repo.BucketRepo;
import com.hivedata.euclid.hilbert.repo.ExpRepo;
import org.slf4j.Logger;
import com.hivedata.euclid.hilbert.controller.date;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.UUID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * <h1>Controller for Bucket Management</h1>
 * The BucketController program handles the elementary CRUD operations for the buckets Management of Hilbert.
 * @author Sachin Raghavendran
 * @version 1.0
 * @since 2018-06-25
 */

///TODODODODODDODO????: BUCKET SIZE VALIDATION


public class BucketController {

    @Autowired
    BucketRepo bucketRepo;


    /**
     * This method returns all of the current buckets along with their respective characteristics. The lastAccessedBy
     * parameter of each bucket will be updated to correspond to the current user. The user validity will be checked
     * in the business logic class. This method does not throw any exception (it assumes the user is a valid user).
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @param repository This corresponds to the BucketRepo that the bucket is pulled from.
     * @return This returns a JSON String representation of all the buckets.
     */
    public static String getAll(String userID, BucketRepo repository) {
        ArrayList<String> answers = new ArrayList<>();
        for (Bucket b: repository.findAll()){
            repository.delete(b);
            repository.save(b);
        }

        return convertObject(repository.findAll());
    }

    /**
     * This method modifies the model that is allotted to the specific bucket (identified by the String-to-UUID bucketID).
     * Also, the lastEditedBy param is modified to the userID. If the bucket is not found (or does not exist), the request call will not be performed.
     * @param bucketID The String (that will be converted to UUID) identification of a bucket.
     * @param newModel This int corresponds to the new model of assignment. For now, the number points to a specific model.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @param repository This corresponds to the BucketRepo that the bucket is pulled from.
     * @return This returns a JSON String representation of the updated bucket.
     */
    public static String changeModel(String bucketID, int newModel, String userID, BucketRepo repository){
        Bucket buck = findByID(bucketID, repository);
        repository.delete(buck);
        buck.setModel(newModel);
        buck.setLastEditedBy(userID);
        repository.save(buck);
        return convertObject(buck);
    }


    /**
     * This is the bucket creation method. Note that all the buckets of an experiment should have a total
     * allocation of 1.0. Additionally, the lastAccessedBy and lastEditedBy will default to "none" which is self-explanatory.
     * Should it be the case that not all the parameters are satisfied, an exception will be thrown and the request call will not proceed.
     * @param experimentID The String (that will be converted to UUID) identification of which experiment the bucket responds to.
     * @param bucketName The String name identification of what the bucket is called.
     * @param description The String description of the bucket.
     * @param allocation The double allocation of what percentage of the data is allotted to the bucket.
     * @param model The int corresponding to the model in this bucket. There will likely be a mapping directory of ints to a model.
     * @param startDate The String start date of the bucket.
     * @param endDate The String end date of the bucket. This must be at or after the startDate.
     * @param status The String status for a bucket. Possible options: notStarted, Started, finished, invalid, etc..
     * @param repository This corresponds to the BucketRepo that the bucket is pulled from.
     * @return This method returns a String establishing that the specific bucket has been saved.
     */
    public static String create(String experimentID, String bucketName, String description, double allocation,
                         int model, String startDate, String endDate, String status, BucketRepo repository, ExpRepo repo2) {
        Bucket bucket = new Bucket(UUIDs.timeBased(), experimentID,  bucketName, description, allocation, model,
                startDate, endDate, "none", "none", status);

        //update the buckets for the experiment
        Experiment exp = findByID(experimentID, repo2);
        repo2.delete(exp);
        exp.addBucket(bucket.getBucketId().toString());
        repo2.save(exp);

        repository.save(bucket);
        return "Bucket Saved\n";
    }

    /**
     * This method finds a specific bucket based on its bucketID and then changes the lastAccessedBy of the bucket to be the
     * corresponding userID. The business logic class should handle validation of the user. An error will be thrown if the bucket
     * cannot be found. Should it be the case that there is erratic input, the request call will not proceed.
     * @param bucketID The String (that will be converted to UUID) identification of a bucket.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @param repository This corresponds to the BucketRepo that the bucket is pulled from.
     * @return This method returns a JSON String representation of the specific bucket.
     */
    public static String find(String bucketID, String userID, BucketRepo repository) {
        Bucket buck = findByID(bucketID, repository);
        return convertObject(buck);
    }


    /**
     * This method leads to the modification of a specific bucket (identified by its bucketID) and changes the lastEditedBy of
     * the specific bucket to be the userID. Should the bucketID not be found, the request call will not be performed.
     * @param bucketID The String (that will be converted to UUID) identification of a bucket.
     * @param newStatus The String corresponding to the newly assigned status for the bucket.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @param repository This corresponds to the BucketRepo that the bucket is pulled from.
     * @return This method returns a JSON String representation of the updated bucket.
     */

    public static String changeStatus(String bucketID, String newStatus,
                                      String userID, BucketRepo repository) {
        Bucket buck = findByID(bucketID, repository);
        repository.delete(buck);
        buck.setLastEditedBy(userID);
        buck.setStatus(newStatus);
        repository.save(buck);
        return convertObject(buck);
    }



    /**
     * This method deletes a specific bucket identified by its specific bucketID. If the bucket is not found or does not exist,
     * an exception will be thrown and the request call will not be completed.
     * @param bucketID The String (that will be converted to UUID) identification of a bucket.
     * @param repository This corresponds to the BucketRepo that the bucket is pulled from.
     * @return This returns a String establishing that the specific bucket has been deleted.
     */
    public static String delete(String bucketID, BucketRepo repository) {
        repository.delete(UUID.fromString(bucketID));

        return "Bucket is deleted!\n";
    }


    /**
     * This method displays all the buckets for a specific experiment (identified by the experiment ID). Should the specific
     * experiment not be found, the request call will not be performed.
     * @param expID The String (that will be converted to UUID) identification of which experiment the bucket responds to.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @param repository This corresponds to the BucketRepo that the bucket is pulled from.
     * @return This method returns a JSON String representation of all the buckets corresponding to the specific experiment.
     */
    public static String bucketsExp(String expID, String userID, BucketRepo repository){
        ArrayList<Bucket> buckets = new ArrayList<>();
        for (Bucket b: repository.findAll()){
            if (b.getExperimentId().equals(expID)){
                buckets.add(b);
            }
        }
        return convertObject(buckets);
    }

    /**
     * This method leads to the modification of a specific bucket (identified by its bucketID) and changes the lastEditedBy of
     * the specific bucket to be the userID. In v 1.0, the userID is represented as a String, although when used in conjunction
     * with the other controllers, it will likely be a true UUID (the conversion from UUID to String is trivial). Should the
     * bucket not be found based on its bucketID, this method will throw an error.
     * @param bucketID The String (that will be converted to UUID) identification of a bucket.
     * @param newAlloc The String corresponding to the newly assigned allocation for the bucket.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @param repository This corresponds to the BucketRepo that the bucket is pulled from.
     * @return This method returns a JSON String representation of the updated bucket.
     */
    public static String changeAlloc(String bucketID, double newAlloc, String userID, BucketRepo repository) {
        Bucket bucket = findByID(bucketID, repository);
        repository.delete(bucket);
        bucket.setLastEditedBy(userID);
        bucket.setAllocation(newAlloc);
        repository.save(bucket);
        return "Bucket size has been changed\n";
    }

    /**
     * This method leads to the modification of a specific bucket (identified by its bucketID) and changes the lastEditedBy of
     * the specific bucket to be the userID. In v 1.0, the userID is represented as a String, although when used in conjunction
     * with the other controllers, it will likely be a true UUID (the conversion from UUID to String is trivial). Should the
     * bucket not be found based on its bucketID, this method will throw an error.
     * @param bucketID The String (that will be converted to UUID) identification of a bucket.
     * @param newAlloc The String corresponding to the newly assigned allocation for the bucket.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @param repository This corresponds to the BucketRepo that the bucket is pulled from.
     * @return This method returns a JSON String representation of the updated bucket.
     */
    public static String changeEndDate(String bucketID, String newEndDate, String userID, BucketRepo repository) {
        Bucket bucket = findByID(bucketID, repository);
        if (!date.validateDates(bucket.startDate(), newEndDate)){
            return "Invalid operation\n";
        }
        repository.delete(bucket);
        bucket.setLastEditedBy(userID);
        bucket.setEndDate(newEndDate);
        repository.save(bucket);
        return "Bucket end date has been changed\n";
    }

    /**
     * This method leads to the modification of a specific bucket (identified by its bucketID) and changes the lastEditedBy of
     * the specific bucket to be the userID. In v 1.0, the userID is represented as a String, although when used in conjunction
     * with the other controllers, it will likely be a true UUID (the conversion from UUID to String is trivial). Should the
     * bucket not be found based on its bucketID, this method will throw an error.
     * @param bucketID The String (that will be converted to UUID) identification of a bucket.
     * @param newAlloc The String corresponding to the newly assigned allocation for the bucket.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @param repository This corresponds to the BucketRepo that the bucket is pulled from.
     * @return This method returns a JSON String representation of the updated bucket.
     */
    public static String changeExperiment(String bucketID, String newExperiment, String userID, BucketRepo repository) {
        Bucket bucket = findByID(bucketID, repository);
        repository.delete(bucket);
        bucket.setLastEditedBy(userID);
        bucket.setExperimentId(newExperiment);
        repository.save(bucket);
        return "Bucket end date has been changed\n";
    }


    /**
     * This method is the all-powerful deletion method of all the buckets in the bucket database. The authorization level
     * of the performer of this action will be verified in the business logic class. No exception is ever thrown.
     * @return This method returns a String establishing the deletion of all and any buckets.
     * @param repository This corresponds to the BucketRepo that the bucket is pulled from.
     */

    public static String deleteAll(BucketRepo repository){
        repository.deleteAll();
        return "All of the Buckets have been deleted\n";
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
    * This method is a utility method for the Bucket Controller class that essentially
    * finds a Bucket by userID from a given BucketRepo. An error is thrown with illegitimate parameters
    * and/or if the bucket is not found in the repository.
    * @param buckID The String (that will be converted to UUID) identification of a bucket.
    * @param repository This corresponds to the BucketRepo that the bucket is pulled from.
    */
    private static Bucket findByID(String buckID, BucketRepo repository){
        for (Bucket buck: repository.findAll()){
            if (buck.getBucketId().equals(UUID.fromString(buckID))){
                return buck;
            }
        }
        return null;
    }

    /**
     * This method makes a copy of the specific bucket, albeit with a different bucketID. This is a very handy method for
     * having multiple buckets created quickly for a specific experiment. Should the bucket not be found based on its bucketID,
     * the request call will not be performed and an error will be thrown.
     * @param bucketID The String (that will be converted to UUID) identification of a bucket.
     * @return This returns a JSON String representation of the "new" bucket-copy.
     */

    /*@PostMapping(path = "/bucketCopy")
    public @ResponseBody String bucketCopy(@RequestParam String bucketID){
        Bucket bucket = this.repository.findOne(UUID.fromString(bucketID));
        Bucket bucket1 = bucket;
        bucket1.setBucketId(UUIDs.timeBased());
        bucket1.setLastAccessedBy("none");
        bucket1.setLastEditedBy("none");
        this.repository.save(bucket1);
        return convertObject(bucket1);
    }*/

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



    //validate a bucket for its size, should be part of biz logic (TODO!!!!)
    //@GetMapping(path = "/validBucket")
    //public @ResponseBody String validBucket(@RequestParam String expID){
    //    double totalBucketSize = 0;
    //   for (Bucket b: this.repository.findAll()){
    //        if (b.getExperimentId().equals(expID)){
    //            totalBucketSize+=b.getAllocation();
    //        }
    //    }
    //    return String.valueOf(totalBucketSize == 1.0 || totalBucketSize == 1) + "\n";
    //}
}

