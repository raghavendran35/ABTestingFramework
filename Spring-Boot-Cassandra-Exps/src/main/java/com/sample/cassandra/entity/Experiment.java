package com.sample.cassandra.entity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.util.UUID;

@Table
public class Experiment {

    @PrimaryKey
    private UUID id;

    @Column
    private String name;


    @Column
    private String startDate;

    @Column
    private String endDate;

    @Column
    private String description;

    @Column
    private String lastAccessedBy;

    @Column
    private String lastEditedBy;

    @Column
    private String status;

    @Column
    private int algId;


    public Experiment() {
    }

    public Experiment(UUID id, String name, String startDate, String endDate,
                      String description, String lastAccessedBy, String lastEditedBy,
                      String status, int algId) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.lastAccessedBy = lastAccessedBy;
        this.lastEditedBy = lastEditedBy;
        this.status = status;
        this.algId = algId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAlgId() {
        return algId;
    }


    public void setAlgId(int algId) {
        this.algId = algId;
    }

    //public JSONObject toJson(){
    //    JSONObject jo = new JSONObject();
        //jo.put("id", getId());
        //jo.put("name", getName());
        //jo.put("startDate", getStartDate());
        //jo.put("endDate", getEndDate());
        //jo.put("description",getDescription());
        //jo.put("lastAccessedBy", getLastAccessedBy());
        //jo.put("lastEditedBy", getLastEditedBy());
        //jo.put("status",getStatus());
        //jo.put("Selection Routine", getAlgId());
        //JsonParser parser = new JsonParser();
    //}


}
