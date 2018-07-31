package com.hivedata.euclid.hilbert.controller;
public class Experiment {
    String session_Token;
    String experiment_ID;
    String bucket_ID;
    Integer selected;
    public Experiment(String session_Token, String experiment_ID, String bucket_ID, Integer selected){
        this.session_Token = session_Token;
        this.experiment_ID = experiment_ID;
        this.bucket_ID = bucket_ID;
        this.selected = selected;
    }
    public String getSession_Token(){
        return session_Token;
    }
    public String getExperiment_ID(){
        return experiment_ID;
    }
    public String getBucket_ID(){
        return bucket_ID;
    }
    public Integer getSelected(){
        return selected;
    }
}
