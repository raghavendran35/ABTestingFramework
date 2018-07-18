package com.hivedata.euclid.hilbert.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.hivedata.euclid.hilbert.entity.User;
import com.hivedata.euclid.hilbert.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <h1>Controller for User Management</h1>
 * The UserController program handles the elementary CRUD operations for the User Management of Hilbert.
 * @author Sachin Raghavendran
 * @version 1.0
 * @since 2018-06-25
 */

public class UserController {

    public static Date date = new Date();

    /**
     * This is a Read Method for getting all current users. It does
     * not throw an exception, but rather returns an empty
     * @param repository This corresponds to the repository that the Users are pulled from.
     * @return String This returns a JSON String of all the current Users.
     */
    public static String getAll(UserRepo repository) {
        return convertObject(repository.findAll());
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
     * @param repository This corresponds to the userRepo that the user is pulled from.
     * @return String This returns a String that establishes whether the User is saved.
     */

    public static String create(String firstName, String lastName, String email, String username,
                         String password, String authLevel, UserRepo repository) {
        User experiment = new User(UUIDs.timeBased(), firstName, lastName, email, username, HashIt(password),"none", authLevel);
        repository.save(experiment);
        return "Saved\n";
    }

    /**
     * This method finds a specific user based on their respective ID. All of their
     * respective attributes such as firstName, lastName, etc. will be revealed. As of version 1.0,
     * this does not catch an exception when a user is not found, but rather will be recorded in the logger
     * and the request call itself will not successfully go through if the parameter is not valid.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @param repository This corresponds to the userRepo that the user is pulled from.
     * @return This returns the specific User represented as a JSON string.
     */
    public static String find(String userID, UserRepo repository) {
        return convertObject(findByID(userID, repository));
    }

    /**
     * This method finds a specific user based on their respective id, and then deletes them. It does not catch an exception
     * if user is not found (v 1.0). However, any behavior is recorded in the logger and the request call will not be
     * successful if the parameter is not valid.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @param repository This corresponds to the userRepo that the user is pulled from.
     * @return This returns a string that establishes whether the user is deleted.
     */

    public static String delete(String userID, UserRepo repository) {
        repository.delete(UUID.fromString(userID));
        return "User is deleted!\n";
    }

    /**
     * This method performs a password change for the a user based on their specific ID. The password will also be changed
     * and then encrypted with SHA-256 as long as the user is valid. Additionally, the lastPasswordChange characteristic of the
     * user is updated to the current date. As of version 1.0, there is no exception caught but
     * rather it is recorded in the logger and the request call will not pass through with any invalid parameters.
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @param newPassword The password that the current password of the user will be changed too.
     * @param repository This corresponds to the userRepo that the user is pulled from.
     * @return This returns a JSON string that shows the new user along with their updated password AND newest password change.
     */
    public static String changePassword(String userID, String newPassword, UserRepo repository){
        User user = findByID(userID, repository);
        repository.delete(UUID.fromString(userID));
        user.setPassword(HashIt(newPassword));
        Date date = new Date();
        String currDate = "" + date.getMonth()+ "-" +date.getDate() + "-" +date.getYear() + 1900 + "::" + date.getTime() + " milliseconds since January 1, 1970";
        user.setLastPasswordChange(currDate);
        repository.save(user);
        return convertObject(user);

    }



    /**
     * This method changes the authorization level of a user (which should only ever be performed as an Admin service).
     * A user is found based on their UUID and without issue, their authorization is relegated or augmented. As of version 1.0, there is no exception catching if the user is not found. Nevertheless, behavior is
     * recorded in the logger and the request call will not be successful should there be erratic input(s).
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @param newAuth The String that represents the new authorization level of the user.
     * @param repository This corresponds to the userRepo that the user is pulled from.
     * @return This method returns the specific user as a JSON string with the new updates.
     */
    public static String authChange(String userID, String newAuth, UserRepo repository){
        User use = findByID(userID, repository);
        repository.delete(use);
        use.setAuthLevel(newAuth);
        repository.save(use);
        return convertObject(use);
    }


    /**
     * This method is the all-powerful clear method for all of the users in the database. The Business Logic class
     * will validate the person who is performing this sudo action. Errors do not occur for this method (as of v 1.0).
     * @param repository This corresponds to the userRepo that the user is pulled from.
     * @return This method returns a string that displays whether all the users have been removed form the database.
     */
    public static String deleteAll(UserRepo repository){
        repository.deleteAll();
        return "All current Users have been deleted.\n";
    }


    /**
    *This method assigns an experiment to a specific user.
    * @param repository This corresponds to the userRepo that the user is pulled from.
    * @param experimentID This String-to-UUID corresponds to the experiment that is itself added.
    * @param userID This String corresponds to the user who will have an experiment added.
    */
    public static String addExptoUser(UserRepo repository, String experimentID, String userID){
        User current = findByID(userID, repository);
        repository.delete(current);
        current.addExperiment(experimentID);
        repository.save(current);
        return "Experiment has been added to user!\n";
    }

    /**
    *This method removes an experiment from a specific user.
    * @param repository Thsi corresponds to the userRepo that the user is pulled form.
    * @param experimentID This String-to-UUID corresponds to the experiment that is itself added.
    * @param userID This String corresponds to the user who will have an experiment added.
    */

    public static String rmExpfromUser(UserRepo repository, String experimentID, String userID){
        User current = findByID(userID, repository);
        repository.delete(current);
        current.rmExperiment(experimentID);
        repository.save(current);
        return "Experiment has been removed from user!\n";
    }

    /**
    *This method assigns a bucket to a specific user.
    * @param repository This corresponds to the userRepo that the user is pulled from.
    * @param bucketID This String-to-UUID corresponds to the experiment that is itself added.
    * @param userID This String corresponds to the user who will have an experiment added.
    */
    public static String addBuckettoUser(UserRepo repository, String bucketID, String userID){
        User current = findByID(userID, repository);
        repository.delete(current);
        current.addBucket(bucketID);
        repository.save(current);
        return "Bucket has been added to user!\n";
    }

    /**
    *This method removes a bucket from a specific user.
    * @param repository This corresponds to the userRepo that the user is pulled from.
    * @param bucketID This String-to-UUID corresponds to the experiment that is itself added.
    * @param userID This String corresponds to the user who will have an experiment added.
    */
    public static String removeBuckUser(UserRepo repository, String bucketID, String userID){
        User current = findByID(userID, repository);
        repository.delete(current);
        current.rmBucket(bucketID);
        repository.save(current);
        return "Bucket has been removed from user!\n";
    }

    /**
    *
    *
    */

    public static String findUserByPassUse(UserRepo repository, String username, String password){
        for (User use: repository.findAll()){
            if (use.getPassword().equals(HashIt(password))&&use.getUsername().equals(username)){
                return convertObject(use);
            }
        }
        return "User does not exist OR You do not have valid login combination\n\n"; 
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

    private static String convertObject(Object obj) throws NullPointerException{
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(obj);
    }

    /**
     * This method is a utility method that hashes a key using the SHA-256 hashing algorithm.
     * @param key A generic String.
     * @return This method returns a String that is the SHA-256 hash of the key
     */
    private static String HashIt(String key) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        digest.update(key.getBytes());
        String encryptedString = new String(digest.digest());
        return encryptedString;
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
     * This method makes a copy of the specific user, but the "new" user has their own unique ID. This is a utility method.
     * As of version 1.0, there is no exception caught if the user is not found. Nevertheless, behavior is recorded in the logger
     * and the request call will not be successful should there be erratic input.
     *
     * @param userID The String (that will be converted to UUID) identification of a user.
     * @return This method returns a JSON String representation of the new user copy.
     */
    /*


    public @ResponseBody String makeCopy(@RequestParam String userID){
        logger.info("A copy of the user is being made.");
        User user = this.repository.findById(UUID.fromString(userID));
        user.setId(com.datastax.driver.core.utils.UUIDs.timeBased());
        this.repository.save(user);
        return convertObject(user);
    }
    */


}
