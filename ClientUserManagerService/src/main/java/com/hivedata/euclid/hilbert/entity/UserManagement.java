package com.hivedata.euclid.hilbert.entity;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.UUID;

@Table
public class UserManagement implements Serializable{

    @PrimaryKey
    private String session_Token;

    @Column
    private String experiment_ID;


    @Column
    private String bucket_ID;


    public UserManagement() {
    }

    public UserManagement(String session_Token, String experiment_ID, String bucketID) {
        this.session_Token=session_Token;
        this.experiment_ID=experiment_ID;
        this.bucket_ID=bucketID;
    }

    public String getSessionToken() {
        return session_Token;
    }

    public void setSessionToken(String session_Token){
    }

    public String getExperimentID() {
        return experiment_ID;
    }

    public void setExperimentID(String experimentID) {
        this.experiment_ID = experimentID;
    }

    public String getBucketID(){
        return bucket_ID;
    }

    public void setBucketID(String bucket_ID){
        this.bucket_ID=bucket_ID;
    }

}
