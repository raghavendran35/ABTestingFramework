package com.sample.cassandra.entity;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;
import java.util.ArrayList;
import java.util.UUID;

@Table
public class Bucket {

    @PrimaryKey
    private UUID bucketID;

    //which experiment it is assigned too
    //note that experimentID is actually a UUID, but is recorded as a string
    @Column
    private String experimentId;


    @Column
    private String bucketName;


    @Column
    private String description;

    @Column
    private double allocation;

    //
    @Column
    private int model;

    @Column
    private ArrayList<Double> allocationHistory = new ArrayList<>();

    @Column
    private String startDate;

    @Column
    private String endDate;

    @Column
    private String lastAccessedBy;

    @Column
    private String lastEditedBy;

    @Column
    private String status;

    public Bucket() {
    }

    public Bucket(UUID BucketID, String experimentId, String bucketName, String description,
                  double allocation, int model, String startDate, String endDate,
                  String lastAccessedBy, String lastEditedBy,
                  String status) {
        this.bucketID = BucketID;
        this.experimentId = experimentId;
        this.bucketName = bucketName;
        this.description = description;
        this.allocation = allocation;
        this.model = model;
        this.allocationHistory.add(allocation);
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastAccessedBy = lastAccessedBy;
        this.lastEditedBy = lastEditedBy;
        this.status = status;
    }

    public UUID getBucketId() {
        return bucketID;
    }

    public void setBucketId(UUID id) {
        this.bucketID = id;
    }

    public String getExperimentId(){
        return experimentId;
    }

    public void setExperimentId(String id){
        this.experimentId = id;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String name) {
        this.bucketName = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public double getAllocation(){
        return allocation;
    }

    public void setAllocation(double allocation){
        this.allocation = allocation;
        this.allocationHistory.add(allocation);
    }


    public ArrayList<Double> getAllocationHistory() {
        return allocationHistory;
    }

    public void setAllocationHistory(ArrayList<Double> allocationHistory) {
        this.allocationHistory = allocationHistory;
    }

    public String startDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLastAccessedBy() {
        return lastAccessedBy;
    }

    public void setLastAccessedBy(String lastAccessedBy) {
        this.lastAccessedBy = lastAccessedBy;
    }

    public String getLastEditedBy() {
        return lastEditedBy;
    }

    public void setLastEditedBy(String lastEditedBy) {
        this.lastEditedBy = lastEditedBy;
    }

    public void setModel(int model){
        this.model = model;
    }

    public int getModel(){
        return this.model;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }





}
