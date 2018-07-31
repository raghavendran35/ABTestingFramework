package com.hivedata.euclid.hilbert.controller;

import org.apache.commons.math3.distribution.BetaDistribution;

import java.util.ArrayList;

public class GreedySelection {
    public String BucketToSelect;

    //this is assuming there are some previous participants of the experiment
    public GreedySelection(String experiment, Integer BucketSize){
        Objectify obj = new Objectify(experiment);
        //get the buckets for the specific experiment
        ArrayList<String> currBuckets = obj.experimentList.getBucketNames();
        ArrayList<Double> clickRates = new ArrayList<>();
        for (String buck: currBuckets){
            Integer currentSelect = obj.experimentList.returnBucketSelected(buck);
            clickRates.add((double) currentSelect/(obj.experimentList.returnBucketSize(buck)));
        }
        this.BucketToSelect = returnBestBucket(currBuckets, clickRates);


    }
    private String returnBestBucket(ArrayList<String> currBuckets, ArrayList<Double> clickRates){
        int bestIndex = 0;
        double currMax = 0;
        for (int i =0; i<clickRates.size();i++){
            if (clickRates.get(i) > currMax ) {
                bestIndex = i;
                currMax = clickRates.get(i);
            }
        }
        return currBuckets.get(bestIndex);
    }
}

