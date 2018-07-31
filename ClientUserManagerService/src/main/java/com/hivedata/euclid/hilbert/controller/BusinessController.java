package com.hivedata.euclid.hilbert.controller;
import com.datastax.driver.core.utils.UUIDs;
import com.hivedata.euclid.hilbert.controller.selectionRoutines;
import java.util.Date;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hivedata.euclid.hilbert.entity.UserManagement;
import com.hivedata.euclid.hilbert.repo.UserManagementRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.UUID;
import java.util.HashMap;
import java.util.Arrays;





@Controller
@RequestMapping(path = "/api/v1.0/usersManagement")
public class BusinessController {
    @Autowired
    private UserManagementRepo userRepo;

    public static ArrayList<String> selectionRoutinesList;

    public static String currentRoutine;

    public static HashMap<String, Double> buckets;

    static
    {
        selectionRoutinesList = new ArrayList<String>();
        selectionRoutinesList.add("ThompsonSampling");
        selectionRoutinesList.add("stochasticRand");
        selectionRoutinesList.add("other");
        currentRoutine = selectionRoutinesList.get(2);
        buckets = new HashMap<String, Double>();
        //if modifying experiment, modify the buckets in the following code
        buckets.put("Button1", 0.5);
        buckets.put("Button2", 0.5);
    }

    @RequestMapping(path="/initUser")
            public @ResponseBody String initUser(@RequestParam String googleID, @RequestParam String experimentID){
        UserManagement newUser=new UserManagement(googleID, experimentID, null);
        //do not want to create new user info
        for (UserManagement use: userRepo.findAll()){
            if (use.getExperimentID().equals(newUser.getExperimentID())&&use.getSessionToken().equals(newUser.getSessionToken())){
                return convertObject(use);
            }
        }
        userRepo.save(newUser);
        return convertObject(newUser);
    }

    @RequestMapping(path="/fullInitUser")
    public @ResponseBody String fullInitUser(@RequestParam String googleID, @RequestParam String experimentID){
        UserManagement newUser=new UserManagement(googleID, experimentID, selectionRoutines.doBucketSelection(currentRoutine));
        for (UserManagement use: userRepo.findAll()){
            if (use.getExperimentID().equals(newUser.getExperimentID())&&use.getSessionToken().equals(newUser.getSessionToken())){
                return convertObject(use);
            }
        }
        userRepo.save(newUser);
        return convertObject(newUser);
    }





    //return Bucket
    @RequestMapping(path="/returnBucket")
    public @ResponseBody String returnBucket(@RequestParam String sessionToken, @RequestParam String experimentID){
        UserManagement user = findByIDandExp(sessionToken, experimentID, userRepo);
        if (user.getBucketID()==null){
            return assignBucket(sessionToken, experimentID);
        }
        return user.getBucketID() +"\n";
    }

    //TODO: handle sampling
    @RequestMapping(path="/assignBucket")
    public @ResponseBody String assignBucket(@RequestParam String sessionToken, @RequestParam String experimentID){
        UserManagement user = findByIDandExp(sessionToken, experimentID, userRepo);
        //change currentRoutine if you want to do something different for selection
        user.setBucketID(selectionRoutines.doBucketSelection(currentRoutine));
        userRepo.save(user);
        return convertObject(user);
    }

    @RequestMapping(path="/getUser")
    public @ResponseBody String getUser(@RequestParam String sessionToken, @RequestParam String experimentID){
        UserManagement user = findByIDandExp(sessionToken, experimentID, userRepo);
        return convertObject(user);
    }

    @RequestMapping(path="/deleteUser")
    public @ResponseBody String deleteUser(@RequestParam String sessionToken, @RequestParam String experimentID){
        UserManagement user = findByIDandExp(sessionToken, experimentID, userRepo);
        userRepo.delete(user);
        return "User is deleted\n";
    }

    @RequestMapping(path="/getExpUsers")
    public @ResponseBody String getExpUsers(@RequestParam String experimentID){
        ArrayList<UserManagement> expUsers= new ArrayList<>();
        for (UserManagement use: userRepo.findAll()){
            if (use.getExperimentID().equals(experimentID)){
                expUsers.add(use);
            }
        }
        return convertObject(expUsers);
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

    private static UserManagement findByIDandExp(String sessionID, String expID, UserManagementRepo repository){
        for (UserManagement use: repository.findAll()){
            if (use.getSessionToken().equals(sessionID)&&use.getExperimentID().equals(expID)){
                return use;
            }
        }
        return null;
    }







}
