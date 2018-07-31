package com.hivedata.euclid.hilbert.controller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hivedata.euclid.hilbert.entity.Analytics;
import com.hivedata.euclid.hilbert.entity.UserManagement;
import com.hivedata.euclid.hilbert.repo.UserManagementRepo;
import com.hivedata.euclid.hilbert.repo.AnalyticsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;





@Controller
@RequestMapping(path = "/buttonTest/Analytics")
public class AnalyticsController {
    @Autowired
    private AnalyticsRepo analyticsRepo;



    @RequestMapping(path="/getExpUsers")
    public @ResponseBody String getExpUsers(@RequestParam String experimentID){
        ArrayList<Analytics> expUsers= new ArrayList<>();
        for (Analytics analytics: analyticsRepo.findAll()){
            if (analytics.getExperimentID().equals(experimentID)){
                expUsers.add(analytics);
            }
        }
        return convertObject(expUsers);
    }


    @RequestMapping(path="/setClicked")
    public @ResponseBody String setClicked(@RequestParam String sessionID,
                                           @RequestParam String experimentID,
                                           @RequestParam String bucketID,
                                           @RequestParam String Clicked){


        Analytics analytics1 = new Analytics(sessionID, experimentID, bucketID, Integer.parseInt(Clicked));
        //if it is already in DB
        UserManagement userNew = new UserManagement(sessionID, experimentID, bucketID);
        for (Analytics analytics: analyticsRepo.findAll()){
            UserManagement userCurrent = new UserManagement(analytics.getSessionToken(),experimentID, bucketID);
            if (userCurrent.equals(userNew)){
                analyticsRepo.delete(analytics);
                analyticsRepo.save(analytics1);
                return convertObject(analytics1);
            }
        }
        analyticsRepo.save(analytics1);
        return convertObject(analytics1);
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
            if (use.getSessionToken().equals(UUID.fromString(sessionID))&&use.getExperimentID().equals(expID)){
                return use;
            }
        }
        return null;
    }







}
