package com.hivedata.euclid.hilbert.entity;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.UUID;

@Table
public class User implements Serializable{

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
    private String authLevel;

    @Column
    private ArrayList<String> experiments = new ArrayList<>();

    @Column
    private ArrayList<String> buckets = new ArrayList<>();

    public User() {
    }

    public User(UUID id, String firstName, String lastName, String email,
                String username, String password, String lastPasswordChange,
                String authLevel) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.lastPasswordChange = lastPasswordChange;
        this.experiments = new ArrayList<>();
        this.experiments.add("none");
        this.authLevel = authLevel;
        this.buckets = new ArrayList<>();
        this.buckets.add("none");
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


    public String getAuthLevel(){
        return authLevel;
    }

    public void setAuthLevel(String authLevel) {
        this.authLevel = authLevel;
    }

    public void addExperiment(String exp){
        this.experiments.add(exp);
    }

    public void rmExperiment(String exp){
        this.experiments.remove(exp);
    }

    public ArrayList<String> getExperiments(){
        return this.experiments;
    }

    public void addBucket(String bucket){
        this.buckets.add(bucket);
    }

    public void rmBucket(String buck){
        this.buckets.remove(buck);
    }

    public ArrayList<String> getBuckets(){
        return this.buckets;
    }



}
