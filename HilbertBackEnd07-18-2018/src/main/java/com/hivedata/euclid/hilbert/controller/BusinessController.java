package com.hivedata.euclid.hilbert.controller;
import com.datastax.driver.core.utils.UUIDs;
import com.hivedata.euclid.hilbert.controller.dates;
import java.util.Date;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hivedata.euclid.hilbert.repo.UserRepo;
import com.hivedata.euclid.hilbert.repo.BucketRepo;
import com.hivedata.euclid.hilbert.repo.ExpRepo;
import com.hivedata.euclid.hilbert.controller.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.hivedata.euclid.hilbert.entity.Bucket;
import com.hivedata.euclid.hilbert.entity.User;
import com.hivedata.euclid.hilbert.entity.Experiment;
import java.util.ArrayList;
import java.util.UUID;
import java.util.HashMap;


@Controller
@RequestMapping(path = "/boss")
public class BusinessController {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BucketRepo bucketRepo;

    @Autowired
    private ExpRepo expRepo;

    private static Logger logger = LoggerFactory.getLogger(BusinessController.class.getName());

    public static int[] models = new int[]{1,3,4,8, 12};
    public static int[] selectionRoutines = new int[]{1,3,4,8, 12};

    public static String topLevel = "adminBoss123";

    public static String org = "hive";

    public static Date date = new Date();


    /**
     * This is a generic method that deletes all of the contained in a specific entity. The deleter
     * must be an admin (for now simulated as having a certain authorization as the keyword).
     * No error is thrown unless the method is called with an illegitimate set of parameter(s).
     * @param category This String corresponds to Experiments, Users and/or Buckets
     * @param userID This String (truly a UUID) corresponds to the User who is acting.
     * @return This returns a String that establishes the success (or failure) of the action.
     */
    @RequestMapping(path="/deleteAll")
    public @ResponseBody String deleteAll(@RequestParam String category, @RequestParam String userID){
        User deleting = findByID(userID, userRepo);
        if (!deleting.getAuthLevel().equals(topLevel)){
            return "You do not have the right authorization!";
        }
        if (category.equals("Experiments")){
            BucketController.deleteAll(bucketRepo);
            return ExperimentController.deleteAll(expRepo);
        }
        else if (category.equals("Users")){
            return UserController.deleteAll(userRepo);

        }
        else if (category.equals("Buckets")){
            return BucketController.deleteAll(bucketRepo);
        }
        return "Invalid Operations!\n";

    }


    /**
     * This is a generic method that finds an entity by name. The viewer
     * must be an admin (for now simulated as having a certain authorization as the keyword).
     * No error is thrown unless the method is called with an illegitimate set of parameter(s).
     * This is a rigorously-defined method as all cases of all letters and characters matter.
     * @param category This String corresponds to Experiments or Buckets
     * @param userID This String (truly a UUID) corresponds to the User who is acting.
     * @return This returns a String that establishes the success (or failure) of the action.
     */
    @RequestMapping(path="/findOneByName")
    public @ResponseBody String findOneByName(@RequestParam String userID, @RequestParam String category, @RequestParam String entityName){
        User finding = findByID(userID, userRepo);
        if (!finding.getAuthLevel().equals(topLevel)){
            return "You do not have the right authorization!";
        }
        if (category.equals("Experiments")){
            return convertObject(findExByName(expRepo, entityName));
        }
        else if (category.equals("Buckets")){
            return convertObject(findBuckByName(bucketRepo, entityName));
        }
        return "Invalid Operations!\n";

    }





    /**
     * This is a viewing method for all the entities (of a specifc category) in the organization. The person
     * viewing must have the right admin level.
     * @param category This String corresponds to the category whose elements will be viewed. This must be either Experiments, Users or Buckets.
     * @param accessingUser This String (truly a UUID) corresponds to the User who is acting.
     * @return This returns a String that establishes the success (or failure) of the action.
     */
    @RequestMapping(path = "/readAll")
    public @ResponseBody String readAll(@RequestParam String category, @RequestParam String accessingUser){
        User user = findByID(accessingUser, userRepo);
        if (user.getAuthLevel().equals(topLevel)&&user.getEmail().contains(org)){
            if (category.equals("Buckets")){
                return BucketController.getAll(accessingUser, bucketRepo);
            }
            else if (category.equals("Users")){
                return UserController.getAll(userRepo);
            }
            else if (category.equals("Experiments")){
                return ExperimentController.getAll(accessingUser,expRepo);
            }
        }
        return "Invalid operation!\n You do not have the right authorization!\n";

    }

    /**
     * This is a viewing method for a certain element of a category in the organization. The person
     * viewing must have the right admin level.
     * @param category This String corresponds to the category whose elements will be viewed. This must be either Experiments, Users or Buckets.
     * @param elementID This String (truly a UUID) corresponds to the experiment, user or bucket.
     * @param accessingUser This String (truly a UUID) corresponds to the specific User who is accessing the element..
     * @return This returns a String that establishes the success (or failure) of the action.
     */
    @RequestMapping(path = "/readOne")
    public @ResponseBody String readOne(@RequestParam String category, @RequestParam String elementID, @RequestParam String accessingUser){
        User user = findByID(accessingUser, userRepo);
        if (user.getAuthLevel().equals(topLevel)&&user.getEmail().contains(org)){
            if (category.equals("Experiment")||(category.equals("Experiments"))){
                return ExperimentController.find(elementID, accessingUser, expRepo); 
            }
            else if (category.equals("User")||(category.equals("Users"))){
                return UserController.find(elementID, userRepo);
            }
            else if (category.equals("Buckets")||(category.equals("Bucket"))){
                return BucketController.find(elementID, accessingUser, bucketRepo);
            }
        }
        return "Invalid operation!\n You do not have the right authorization!\n";
    }

    /**
     * This is a viewing method for all the users in the organization. The person
     * viewing must have the right admin level.
     * @param bucketID This String (truly a UUID) corresponds to the Bucket (for the specific user).
     * @param expID This String (truly a UUID) corresponds to the Experiment (for the specific user).
     * @param accessingUser This String (truly a UUID) corresponds to the User who is acting.
     * @return This returns a String that establishes the success (or failure) of the action.
     */
    @RequestMapping(path = "/users/findUserByExpBuck")
    public @ResponseBody String findUserByExpBuck(@RequestParam String bucketID, @RequestParam String expID, @RequestParam String accessingUser){
        User accessor = findByID(accessingUser, userRepo);
        Bucket buck = findByID(bucketID, bucketRepo);
        if (accessor.getAuthLevel().equals(topLevel)){
            for (User u: userRepo.findAll()){
                if (u.getExperiments().contains(expID)&&u.getBuckets().contains(bucketID)
                    &&buck.getExperimentId().equals(expID)){
                    return convertObject(u);
                }
            }
        }
        return "Invalid operation!\n You do not have the right authorization!\n";
    }


    /**
     * This method is the pivotal Experiment creation method. Errors will be thrown if the required parameters
     * in the request call are not called and if the start and end dates are not valid.
     * @param name This String corresponds to the name of the experiment.
     * @param startDate This String corresponds to the start date of the experiment. Has to be of the following formats: "mm-dd" or "mm-dd-yyyy".
     * @param endDate This String corresponds to the end date of the experiment. Has to be of the following formats: "mm-dd" or "mm-dd-yyyy".
     * @param description This String corresponds to a short description of the experiment.
     * @param status This String corresponds to the status of the experiment. Default should be something tending to "notStarted".
     * @param algId This int corresponds to the selection routine for the buckets of the experiment.
     * @return This returns a string establishing whether the experiment has been successfully saved.
     */
    @RequestMapping(path = "/experiments/addOneExperiment")
    public @ResponseBody String createExp(@RequestParam String name, @RequestParam String startDate,
                                       @RequestParam String endDate, @RequestParam String description,
                                       @RequestParam String status, @RequestParam int algID){
        //TODO?: Verification of algID/selection routine
        if (!dates.validateDates(startDate, endDate)){
            return "Invalid Operation\n";
        }
        return ExperimentController.create(name, startDate, endDate, description, status, algID, expRepo);

    }

    /**
     * This method creates a user and adds it to the current User Management database.
     * Note that the list of experiment names and the list of experiment UUIDs assigned to
     * the user will have nothing initially. The password will be encrypted using the SHA-256 hash and password
     * authentication will be performed by hash-matching. No exception is caught (as of release 1.0) as the request
     * call itself will not parse successfully with erratic input and thus there will be no response.
     * @param firstName The first name for a user.
     * @param lastName The last name for a user.
     * @param email The email/org identification.
     * @param username The username for the user.
     * @param password The password for the user.
     * @param authLevel The authorization level of the user.
     * @return String This returns a String that establishes whether the User is saved.
     */
    @RequestMapping(path = "/users/addOneUser")
    public @ResponseBody String createUser(@RequestParam String firstName, @RequestParam  String lastName,
                                           @RequestParam String email, @RequestParam  String username,
                                           String password, String authLevel){
        if (!email.toLowerCase().contains(org)){
            return "This is not a valid user\n";
        }
        return UserController.create(firstName, lastName, email, username, password, authLevel, userRepo);
    }


    /**
     * This is the bucket creation method. Note that all the buckets of an experiment should have a total
     * allocation of 1.0. Additionally, the lastEditedBy will default to "none" which is self-explanatory.
     * Should it be the case that not all the parameters are satisfied, an exception will be thrown and the request call will not proceed.
     * @param experimentID The String (that will be converted to UUID) identification of which experiment the bucket responds to.
     * @param bucketName The String name identification of what the bucket is called.
     * @param description The String description of the bucket.
     * @param allocation The double allocation of what percentage of the data is allotted to the bucket.
     * @param model The int corresponding to the model in this bucket. There will likely be a mapping directory of ints to a model.
     * @param startDate The String start date of the bucket.
     * @param endDate The String end date of the bucket. This must be at or after the startDate.
     * @param status The String status for a bucket. Possible options: notStarted, Started, finished, invalid, etc..
     * @return This method returns a String establishing that the specific bucket has been saved.
     */

    @RequestMapping(path = "/buckets/addOneBucket")
    public @ResponseBody String createBuck(@RequestParam String experimentID, @RequestParam  String bucketName,
                                           @RequestParam String description, @RequestParam double allocation,
                                           @RequestParam int model, @RequestParam String startDate,
                                           @RequestParam String endDate, @RequestParam String status){
        //TODO: Verification of model validity
        //TODO: Verification of experiment validity (does it the experiment bucket is assigned to exist)
        if (!dates.validateDates(startDate, endDate)){
            return "Invalid Operation\n";
        }  
        return BucketController.create(experimentID, bucketName, description, allocation,
                                    model, startDate, endDate, status, bucketRepo, expRepo);
    }



    //@PutMapping(path = "/put")
    //for user change of password, only user themselves and admin can access/change
    /**
     * This method performs an element change for the a user based on their specific ID. The password will also be changed
     * and then encrypted with SHA-256 as long as the user is valid. Additionally, the lastPasswordChange characteristic of the
     * user is updated to the current date. The authorization level can only be changed if the user has the top authorization level.
     * As of version 1.0, there is no exception caught but rather it is recorded in the logger and the request call will not pass through with any invalid parameters.
     * @param accessingUser The String (that will be converted to UUID) identification of the user Accessing.
     * @param userToChange The String (that will be converted to UUID) identification of a user whose quality will be changed.
     * @param category The specific category of the user (which will be changed).
     * @param newEntity The new value that the specific category of the user will be changed too.
     * @return This returns a JSON string that shows the new user along with their updated password AND newest password change.
     */
    @RequestMapping(path = "/changeUser")
    public @ResponseBody String changeUser(@RequestParam String accessingUser,
                                               @RequestParam String userToChange,
                                               @RequestParam String category,
                                               @RequestParam String newEntity){
        User user = findByID(accessingUser, userRepo);
        if (user.getAuthLevel().equals(topLevel)||accessingUser.equals(userToChange)){
            if (category.equals("password")||category.equals("Password")){

            return UserController.changePassword(userToChange, newEntity, userRepo);
            }
            else if ((category.equals("authLevel")||category.equals("AuthLevel")&&user.getAuthLevel().equals(topLevel))){
                return UserController.authChange(userToChange, newEntity, userRepo);
            }

        }
        return "Invalid operation\n";
    }
    

    /**
     * This method modifies a specific experiment (identified by the String-to-UUID expID). Also, the lastEditedBy param is modified to be the userID. If the bucket is not found (or does not exist),
     * the request call will not be performed. Categories may include endDate, bucket selectionRoutine of the experiment, and status modification. This is a generic
     * experiment modification method.
     * @param expID The String (that will be converted to UUID) identification of a experiment.
     * @param newEntity This String corresponds to the changed quality of the experiment.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @return This returns a JSON String representation of the updated experiment.
     */
    @RequestMapping(path = "/changeExperiment")
    public @ResponseBody String changeExperiment(@RequestParam String expID, @RequestParam String category,
                                                @RequestParam String newEntity, @RequestParam String accessingUser){
        //TODO: VALIDATE that new END DATE IS AFTER OR ON START DATE
        User user = findByID(accessingUser, userRepo);
        Experiment exp = findByID(expID, expRepo);

        if (user.getAuthLevel().equals(topLevel)){
            if (category.equals("endDate")||category.equals("EndDate")){
                if (!dates.validateDates(exp.getStartDate(), newEntity)){
                return "Invalid Operation\n Invalid dates";
                }
                return ExperimentController.changeEndDate(expID, newEntity, accessingUser, expRepo);
            }
            else if (category.equals("selectionRoutine")||category.equals("routine")){
                return ExperimentController.changeRoutine(expID, Integer.parseInt(newEntity), accessingUser, expRepo);
            }
            else if (category.equals("status")||(category.equals("Status"))){
                return ExperimentController.changeStatus(expID, newEntity, accessingUser, expRepo);
            }
            else if (category.equals("name")||(category.equals("Name"))){
                return ExperimentController.changeName(expID, newEntity, accessingUser, expRepo);
            }
            else if (category.equals("description")||(category.equals("Description"))){
                return ExperimentController.changeDescription(expID, newEntity, accessingUser, expRepo);
            }
            else return "Invalid Operation\n";
        }
        return "Wrong Authorization! Invalid Operation\n";
    }


   /**
     * This method leads to the modification of a specific bucket (identified by its bucketID) and changes the lastEditedBy of
     * the specific bucket to be the userID. In v 1.0, the userID is represented as a String, although when used in conjunction
     * with the other controllers, it will likely be a true UUID (the conversion from UUID to String is trivial). Should the
     * bucket not be found based on its bucketID, this method will throw an error.
     * @param bucketID The String (that will be converted to UUID) identification of a bucket.
     * @param newAlloc The String corresponding to the newly assigned allocation for the bucket.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @return This method returns a JSON String representation of the updated bucket.
     */
   @RequestMapping(path = "/changeBucket")
    public @ResponseBody String changeBucket(@RequestParam String accessingUser,  @RequestParam String bucketID, 
                                             @RequestParam String category, @RequestParam String newEntity){
        User user = findByID(accessingUser, userRepo);
        Bucket bucket = findByID(bucketID, bucketRepo);
        if (!user.getAuthLevel().equals(topLevel)){
            return "Invalid Operation\n";
        }
        if (category.equals("endDate")){
            if (!dates.validateDates(bucket.startDate(), newEntity)){
            return "Invalid Operation\n Invalid dates";
            }
            return BucketController.changeEndDate(bucketID, newEntity, accessingUser, bucketRepo);
        }
        else if (category.equals("model")){
            return BucketController.changeModel(bucketID, Integer.parseInt(newEntity), accessingUser, bucketRepo);
        }
        else if (category.equals("allocation")){
            return BucketController.changeAlloc(bucketID, Double.parseDouble(newEntity), accessingUser, bucketRepo);
        }
        else if (category.equals("status")){
            return BucketController.changeStatus(bucketID, newEntity, accessingUser, bucketRepo);
        }
        else if (category.equals("name")){
            return BucketController.changeName(bucketID, newEntity, accessingUser, bucketRepo);
        }
        else if (category.equals("description")){
            return BucketController.changeDescription(bucketID, newEntity, accessingUser, bucketRepo);
        }
        return "Invalid Operation\n";
    }



    //delete a experiment by ID (have to be admin level) and name, then must scrap all buckets attached
    //to the experiment
    /**
     * This method removes an experiment (identified by ID) and all associated buckets. Also, this method finds 
     * a specific user based on their respective id, and then deletes them. This method can also delete a specific bucket.
     * Errors will be thrown if the user who is accessing is not found and/or if the experiment itself cannot be found.
     * @param category The specific category whose element will be deleted (can be 'experiment', 'bucket' or 'user')
     * @param elementID The String (that will be converted to UUID) identification of a bucket, experiment or user.
     * @param userAccessing The String (that will be converted to UUID) identification of the accessor.
     * @return This method returns a String validating the deletion of the experiment.
     */
    @RequestMapping(path = "/deleteOne")
    public @ResponseBody String deleteOne(@RequestParam String accessingUser, @RequestParam String category, @RequestParam String elementID){
        User accessing = findByID(accessingUser, userRepo);
        if (accessing.getAuthLevel().equals(topLevel)){
            
            if (category.equals("experiments")||(category.equals("Experiments"))){
                //delete all references of expID for the buckets corresponding to this experiment
                for (String buckets: findByID(elementID,expRepo).getBuckets()){
                    if (buckets.equals("none")){
                        continue;
                    }
                    //delete buckets if deleting experiments, no identity for buckets if no experiment
                    else {
                        Bucket buck = findByID(buckets, bucketRepo);
                        //delete the actual bucket
                        BucketController.delete(buck.getBucketId().toString(), bucketRepo);
                        //remove bucket from user
                        UserController.removeBuckUser(userRepo, buck.getBucketId().toString(), accessingUser);
                        //delete reference of experiment(for bucket) from user
                        UserController.rmExpfromUser(userRepo, buck.getExperimentId(), accessingUser);
                    }
                }
                return ExperimentController.delete(elementID, expRepo);
            }
            else if (category.equals("buckets")||(category.equals("Buckets"))){
                //take care to delete all of the experiments memory of the now-deleted bucket
                for (Experiment exp: expRepo.findAll()){
                    if (exp.getBuckets().contains(elementID)){
                        ExperimentController.deleteBucket(exp.getId().toString(), elementID, expRepo);
                    }
                }
                //delete bucket (and its experiment compatriot) from users (if it belongs to a user)
                for (User user: userRepo.findAll()){
                    if (user.getBuckets().contains(elementID)){
                        userRepo.delete(user);
                        user.rmBucket(elementID);
                        user.rmExperiment(findByID(elementID, bucketRepo).getExperimentId());
                        userRepo.save(user);
                        break;
                    }
                }
                //delete the bucket itself from the bucketRepo
                return BucketController.delete(elementID, bucketRepo);


            }
            else if (category.equals("users")||(category.equals("Users"))){
                return UserController.delete(elementID, userRepo);
            }
            else return "Invalid Operation\n";
            
        }
        return "Invalid Operation\n";
    }


    /**
     * This method displays all the buckets for a specific experiment (identified by the experiment ID). Should the specific
     * experiment not be found or the user who is accessing not have the right authorization level, the request call will not be performed.
     * @param expID The String (that will be converted to UUID) identification of which experiment the bucket responds to.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @return This method returns a JSON String representation of all the buckets corresponding to the specific experiment.
     */
    @RequestMapping(path="/experiments/allBuckets")
    public @ResponseBody String getExpsBuckets(@RequestParam String expID, @RequestParam String accessingUser){
        //user themselves
        User user = findByID(accessingUser, userRepo);
        //either userID corresponds to owner of the experiment or is admin
        if (user.getAuthLevel().equals(topLevel)){
            return BucketController.bucketsExp(expID, accessingUser, bucketRepo);
        }
        return "Invalid Operation\n";
    }

    /**
     * This method adds a specific bucket to a user. It will automatically add the specific
     * experiment to the user's history. Multiple buckets from the same experiment cannot be
     * added. If that is the case, no error will be thrown but the operation will be stopped.
     * @param bucketID The String (that will be converted to UUID) identification of which experiment the bucket responds to.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @return This method returns a String validating the success of this operation.
     */
    @RequestMapping(path="/users/addOneBucket")
    public @ResponseBody String addBucketUser(@RequestParam String bucketID, @RequestParam String userID){
        //user themselves
        User user = findByID(userID, userRepo);
        //below wil validate existence of an bucket
        Bucket bucket = findByID(bucketID, bucketRepo);
        //either userID corresponds to owner of the bucket or is admin
        if (user.getAuthLevel().equals(topLevel)&&!user.getExperiments().contains(bucket.getExperimentId())){
            //add experiemnt to the user
            UserController.addExptoUser(userRepo, bucket.getExperimentId(), user.getId().toString());
            //add bucket to the user
            return UserController.addBuckettoUser(userRepo, bucketID, user.getId().toString());
        }
        return "Invalid Operation\n";
    }

    /**
     * This method adds a specific bucket to an experiment. If that is the case, no error will be thrown but the operation will be stopped.
     * @param bucketID The String (that will be converted to UUID) identification of which experiment the bucket responds to.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @return This method returns a String validating the success of this operation.
     */
    @RequestMapping(path="/experiments/addOneBucket")
    public @ResponseBody String addBucketExp(@RequestParam String bucketID, @RequestParam String expID, @RequestParam String userID){
        //user themselves
        User user = findByID(userID, userRepo);
        //below wil validate existence of an bucket
        Bucket bucket = findByID(bucketID, bucketRepo);
        //delete bucket reference of previous experiment
        ExperimentController.deleteBucket(bucket.getExperimentId().toString(),bucket.getBucketId().toString(),expRepo);
        //delete old experiment reference from user who is owner (dont need to delete old bucket reference from user)
        UserController.rmExpfromUser(userRepo, bucket.getExperimentId().toString(), userID);
        //change experiment of the current bucket
        BucketController.changeExperiment(bucketID, expID, userID, bucketRepo);
        
        //either userID corresponds to owner of the bucket or is admin
        if (user.getAuthLevel().equals(topLevel)){
            //physically add the bucket to the experiment's bucket list
            return ExperimentController.addBucket(expID, bucketID, expRepo);
        }
        return "Invalid Operation\n";
    }

    /**
     * This method removes a specific bucket from a user. It will also remove the experiment corresponding to the bucket
     * from the user.
     * @param bucketID The String (that will be converted to UUID) identification of which experiment the bucket responds to.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @return This method returns a String validating the success of this operation.
     */
    @RequestMapping(path="/users/rmOneBucket")
    public @ResponseBody String removeBuckUser(@RequestParam String userID, @RequestParam String bucketID){
        //user themselves
        User user = findByID(userID, userRepo);
        //below wil validate existence of an experiment
        Bucket bucket = findByID(bucketID, bucketRepo);

        Experiment exp = findByID(bucket.getExperimentId(), expRepo);
        //either userID corresponds to owner of the experiment or is admin
        if (user.getAuthLevel().equals(topLevel)){
            ExperimentController.deleteBucket(exp.getId().toString(), bucketID, expRepo);
            //remove experiment from user
            UserController.rmExpfromUser(userRepo, bucket.getExperimentId(), user.getId().toString());
            //remove bucket from user
            return UserController.removeBuckUser(userRepo, bucketID, user.getId().toString());
        }
        return "Invalid Operation\n";
    }


    /**
     * This method determines the validity of the buckets in an experiment.
     * @param expID The String (that will be converted to UUID) identification of which experiment the bucket responds to.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @return This method returns a String validating the success of this operation.
     */
    @RequestMapping(path="/experiments/validSizes")
    public @ResponseBody String validateBuckets(@RequestParam String userID, @RequestParam String expID){
        //user themselves
        User user = findByID(userID, userRepo);
        //below wil validate existence of an experiment
        Experiment exp = findByID(expID, expRepo);
        
        //either userID corresponds to owner of the experiment or is admin
        if (user.getAuthLevel().equals(topLevel)){
                double totalAlloc = 0;
            for (String b: exp.getBuckets()){
                if (b.equals("none")){
                    continue;
                }
                
                else {
                    Bucket x = findByID(b, bucketRepo);
                    totalAlloc = totalAlloc + x.getAllocation();
                    }
                }
            return "" + (totalAlloc == 1.0) + "\n";


             }
        return "Invalid Operation\n";
    }


    //get all the buckets-experiments combo
    @RequestMapping(path = "/buckets/getAllBuckExps")
    public @ResponseBody String getBuckExps(){
        HashMap<String, String> buckExps = new HashMap<String, String>();
        for (Bucket buck:bucketRepo.findAll()){
            Experiment exp = findByID(buck.getExperimentId(), expRepo);
            buckExps.put(buck.getBucketName(), exp.getName());
        }
        return convertObject(buckExps);
    }


    //validate the existence of a user by the login password combination
    @RequestMapping(path = "/users/validUser")
    public @ResponseBody String validateUser(@RequestParam String username, @RequestParam String password){
        return UserController.findUserByPassUse(userRepo, username, password);
    }











//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                      UTILITY AND OTHER METHODS                                    
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
    /**
     * This utility method gets all the experiment IDs for the buckets in a specific repository.
     * @param repository This corresponds to the BucketRepo that the buckets are pulled from.
     */
    private static ArrayList<UUID> getBucketExps(BucketRepo repository){
        ArrayList<UUID> result = new ArrayList<>();
        for (Bucket b: repository.findAll()){
            result.add(UUID.fromString(b.getExperimentId()));
        }
        return result;
    }

    /**
     * This utility method gets a bucket by name.
     * @param repository This corresponds to the BucketRepo that the buckets are pulled from.
     */
    private static Bucket findBuckByName(BucketRepo repository, String bucketName){
        for (Bucket b: repository.findAll()){
            if (b.getBucketName().equals(bucketName)){
                return b;
            }
        }
        return null;
    }

    /**
     * This utility method gets a bucket by name.
     * @param repository This corresponds to the BucketRepo that the buckets are pulled from.
     */
    private static Experiment findExByName(ExpRepo repository, String expName){
        for (Experiment exp: repository.findAll()){
            if (exp.getName().equals(expName)){
                return exp;
            }
        }
        return null;
    }


    /**
     * This utility method removes all the buckets of an experiment.
     * @param repository This corresponds to the BucketRepo that the buckets are pulled from.
     */
    private static void removeBucketofExp(BucketRepo repository, String ExpID){
        for (Bucket b: repository.findAll()){
            if (b.getExperimentId().equals(ExpID)){
                repository.delete(b);
            }
        }
        return;
    }


    /**
     * This method converts and POJO to a JSON String using Google's GSON libraries. This method throws a null pointer
     * exception should the object be null.
     * @param obj A POJO.
     * @return This method returns a JSON string representation of the POJO.
     */
    private static String convertObject(Object obj) throws NullPointerException{
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(obj);
    }


    /**
    * This method is a utility method for the User Controller class that essentially
    * finds a User by userID from a given UserRepo. An error is thrown with illegitimate parameters
    * and/or if the user is not found in the repository.
    * @param userID The String (that will be converted to UUID) identification of a user.
    * @param repository This corresponds to the userRepo that the user is pulled from.
    */

    private static User findByID(String userID, UserRepo repository){
        for (User user: repository.findAll()){
            if (user.getId().equals(UUID.fromString(userID))){
                return user;
            }
        }
        return null;
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





}
