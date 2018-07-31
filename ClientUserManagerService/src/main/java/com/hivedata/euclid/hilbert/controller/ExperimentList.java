package com.hivedata.euclid.hilbert.controller;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//ASSUME SPECIFIC EXPERIMENT IS CHOSEN BASED ON CODE FROM Objectify.java
public class ExperimentList {
    List<Experiment> experiments;
    public ExperimentList(){
        experiments = new ArrayList<Experiment>();
    }

    public Integer returnSize(){
        return this.experiments.size();
    }
    public Integer returnBucketSize(String bucket){
        int count = 0;
        for (Experiment exp:this.experiments){
            if (exp.getBucket_ID().equals(bucket)){
                count+=1;
            }
        }
        return count;
    }

    public Integer returnSelected(){
        int count = 0;
        for (Experiment exp: this.experiments){
            if (exp.getSelected()==1){
                count+=1;
                continue;
            }
        }
        return count;
    }

    public Integer returnBucketSelected(String bucket){
        int count = 0;
        for (Experiment exp: this.experiments){
            if (exp.getSelected()==1&&exp.getBucket_ID().equals(bucket)){
                count+=1;
                continue;
            }
        }
        return count;
    }

    public ArrayList<String> getBucketNames(){
        ArrayList<String> allBuckets = new ArrayList<>();
        Set<String> currBuckets = new HashSet<>();
        for (Experiment exp: this.experiments) {
            currBuckets.add(exp.getBucket_ID());
        }
        while (currBuckets.iterator().hasNext()){
            allBuckets.add(currBuckets.iterator().next());
        }
        return allBuckets;
    }

    public static void main(String[] args){

    }
}
