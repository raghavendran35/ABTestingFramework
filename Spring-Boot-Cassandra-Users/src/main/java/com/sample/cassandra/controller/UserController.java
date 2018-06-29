package com.sample.cassandra.controller;

import com.datastax.driver.core.querybuilder.Update;
import com.datastax.driver.core.utils.UUIDs;
import com.sample.cassandra.entity.User;
import com.sample.cassandra.repo.UserRepo;
import com.sample.cassandra.repo.UserRepo;
import org.slf4j.Logger;

import java.time.format.DateTimeFormatter;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.UUID;


@Controller
@RequestMapping(path = "/Users")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class.getName());

    @Autowired
    private UserRepo repository;


    //Getting all Users
    @GetMapping(path = "/allUsers")
    public @ResponseBody Iterable<User> getAll() {
        logger.info("User list is checking from cassandra");
        return this.repository.findAll();
    }




    //adding a user, note that experiment allocation list(s) will have nothing initially
    @GetMapping(path = "/addOneUser")
    public @ResponseBody String create(@RequestParam String firstName, @RequestParam String lastName,
                  @RequestParam String email, @RequestParam String username,
                  @RequestParam String password, @RequestParam String lastPasswordChange,
                  @RequestParam String authLevel) {
        User experiment = new User(UUIDs.timeBased(), firstName, lastName, email, username, password, lastPasswordChange, null,null, authLevel);
        logger.info("User has been created and added.");
        this.repository.save(experiment);
        return "Saved\n";
    }

    //Finding a specific experiment
    @GetMapping(path = "/findOneUser")
    public @ResponseBody  User find(@RequestParam String userID) {
        logger.info("User delete process has started.");
        return this.repository.findById(UUID.fromString(userID));
    }



    //DELETE and Experiment operation

    @GetMapping(path = "/deleteOneUser")
    public @ResponseBody String delete(@RequestParam String userID) {
        logger.info("User delete process has started.");
        this.repository.delete(UUID.fromString(userID));
        return "User is deleted!\n";
    }


    //assign an experiment to a user

    //update and Change the password of a user, update lastPasswordChange
    @GetMapping(path = "/changePassword")
    public @ResponseBody String changePassword(@RequestParam String userID, @RequestParam String newPassword){
        logger.info("User password has been changed");
        User user = this.repository.findById(UUID.fromString(userID));
        this.repository.delete(UUID.fromString(userID));
        user.setPassword(newPassword);
        Date date = new Date();
        String currDate = "" + date.getMonth()+ "-" +date.getDate() + "-" +date.getYear()+ "::" + date.getTime();
        user.setLastPasswordChange(currDate);
        this.repository.save(user);
        return "Experiment Status has been changed\n";

    }

    //make a copy of a specific user, but different id
    @GetMapping(path = "/makeCopy")
    public @ResponseBody String makeCopy(@RequestParam String userID){
        User user = this.repository.findById(UUID.fromString(userID));
        user.setId(com.datastax.driver.core.utils.UUIDs.timeBased());
        this.repository.save(user);
        return "Copy of User has been saved\n";

    }

    //note that expName corresponds to id, and expUser corresponds to name, I messed up, I know!
    //User Experiments
    @GetMapping(path="/addExp")
    public @ResponseBody String addExp(@RequestParam String userID, @RequestParam String expID, @RequestParam String expName){
        User user = this.repository.findById(UUID.fromString(userID));
        this.repository.delete(user);
        user.addExpName(expName);
        user.addExpIDs(expID);
        this.repository.save(user);
        return "Experiments has been added to User\n";

    }


    //delete an experiment from a user
    //expName corresponds to UUID, expUser corresponds to Name
    @GetMapping(path="/rmExp")
    public @ResponseBody String rmExp(@RequestParam String userID, @RequestParam String expID, @RequestParam String expName){
        User user = this.repository.findById(UUID.fromString(userID));
        this.repository.delete(user);
        user.getExpIDs().remove(UUID.fromString(expID));
        user.getExpNames().remove(expName);
        this.repository.save(user);
        return "Experiments has been removed from User\n";
    }


    //delete all experiments from a user

    @GetMapping(path="/rmAllExp")
    public @ResponseBody String rmAllExp(@RequestParam String userID){
        User user = this.repository.findById(UUID.fromString(userID));
        this.repository.delete(user);
        user.setExpNames(null);
        user.setExpIDs(null);
        this.repository.save(user);
        return "Experiments have been cleared to User\n";
    }


    //list out the experiments for each user

    @GetMapping(path="/UserSummary")
    public @ResponseBody ArrayList<String> expSummary(@RequestParam String userID){
        return this.repository.findById(UUID.fromString(userID)).getExpNames();
    }

    //change admin level of user
    @GetMapping(path="/ChangeLevel")
    public @ResponseBody String adminChange(@RequestParam String userID, @RequestParam String newAuth){
        User use = this.repository.findById(UUID.fromString(userID));
        this.repository.delete(use);
        use.setAuthLevel(newAuth);
        this.repository.save(use);
        return "User admin level has been changed\n";
    }




    //Find the Ids and Names of all current Users
    @GetMapping(path = "/IdNames")
    public @ResponseBody Iterable<String> IdNames(){
        ArrayList<String> allInfo = new ArrayList<>();
        ArrayList<User> allUsers = (ArrayList<User>) this.repository.findAll();
        for (User use: allUsers){
            allInfo.add("Id: "+ use.getId().toString() + ", Names: " + use.getFirstName() + " " + use.getLastName());
        }
        return allInfo;

    }



    //Delete all users, but should give some sort of sudo rights
    @GetMapping(path = "/deleteAll")
    public @ResponseBody String deleteAll(){
        this.repository.deleteAll();
        return "All current Users have been deleted.\n";
    }







}

