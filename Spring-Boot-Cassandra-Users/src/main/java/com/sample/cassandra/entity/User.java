package com.sample.cassandra.entity;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;
import java.util.ArrayList;

import java.util.UUID;

@Table
public class User {

    @PrimaryKey
    private UUID id;

    @Column
    private String firstName;


    @Column
    private String lastName;

    @Column
    private String email;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String lastPasswordChange;

    @Column
    private ArrayList<String> expNames;

    @Column
    private ArrayList<UUID> expIDs;

    @Column
    private String authLevel;


    public User() {
    }

    public User(UUID id, String firstName, String lastName, String email,
                String username, String password, String lastPasswordChange,
                ArrayList<String> expNames, ArrayList<UUID> expIDs, String authLevel) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.expNames = expNames;
        this.lastPasswordChange = lastPasswordChange;
        this.expIDs=expIDs;
        this.authLevel = authLevel;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getLastPasswordChange(){
        return lastPasswordChange;
    }

    public void setLastPasswordChange(String date) {
        this.lastPasswordChange = date;
    }

    public ArrayList<String> getExpNames() {
        return expNames;
    }

    public void addExpName(String expName){
        this.expNames.add(expName);
    }

    public void setExpNames(ArrayList<String> expNames){
        this.expNames = expNames;
    }

    public void addExpIDs(String expID){
        this.expIDs.add(UUID.fromString(expID));
    }

    public ArrayList<UUID> getExpIDs() {
        return expIDs;
    }

    public void setExpIDs(ArrayList<UUID> expNames){
        this.expIDs = expNames;
    }

    public String getAuthLevel(){
        return authLevel;
    }

    public void setAuthLevel(String authLevel) {
        this.authLevel = authLevel;
    }


}
