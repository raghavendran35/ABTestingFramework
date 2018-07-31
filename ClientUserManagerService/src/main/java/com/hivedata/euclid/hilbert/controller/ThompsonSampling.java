package com.hivedata.euclid.hilbert.controller;
import java.util.ArrayList;
import org.apache.commons.math3.distribution.BetaDistribution;

public class ThompsonSampling {
    //changed Factors
    //

    //startThompson will likely be done elsewhere

    public String BucketToSelect;

    //this is assuming there are some previous participants of the experiment
    public ThompsonSampling(String experiment, Integer BucketSize){
        Objectify obj = new Objectify(experiment);
        //get the buckets for the specific experiment
        ArrayList<String> currBuckets = obj.experimentList.getBucketNames();
        //initialize "reward", "fail" numbers for ref
        ArrayList<Integer> clickedRates = new ArrayList<>();
        ArrayList<Integer> notClickedRates = new ArrayList<>();
        for (String buck: currBuckets){
            Integer currentSelect = obj.experimentList.returnBucketSelected(buck);
            clickedRates.add(currentSelect);
            notClickedRates.add(obj.experimentList.returnBucketSize(buck)-currentSelect);
        }
        //sample beta distribution for each of the buckets based on the parameters of clicked/notClicked
        Double[] vals = new Double[currBuckets.size()];
        for (int i = 0; i < vals.length; i++){
            BetaDistribution beta = new BetaDistribution(clickedRates.get(i) + 1, notClickedRates.get(i) + 1);
            vals[i] = beta.sample();
        }
        this.BucketToSelect = returnBestBucket(currBuckets, vals);


    }
    private String returnBestBucket(ArrayList<String> currBuckets, Double[] betaSamples){
        int bestIndex = 0;
        double currMax = 0;
        for (int i =0; i<betaSamples.length;i++){
            if (betaSamples[i] > currMax ) {
                bestIndex = i;
                currMax = betaSamples[i];
            }
        }
        return currBuckets.get(bestIndex);
    }
}
