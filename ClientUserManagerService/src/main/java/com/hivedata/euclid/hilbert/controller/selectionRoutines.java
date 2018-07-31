package com.hivedata.euclid.hilbert.controller;
import java.util.HashMap;
import java.util.Random;

public class selectionRoutines {

    //bucket with allocations and stuff
    public static Random rand = new Random();
    public static String currExperiment1 = "buttonTest";



    public static String doBucketSelection(String method){
        if (method.equals("stochasticRand")){
            return randSelect(BusinessController.buckets);
        }
        if (method.equals("ThompsonSampling")){
            return ThomsponSample(BusinessController.buckets);
        }

        if (method.equals("greedySelection")){
            return greedySelect(BusinessController.buckets);
        }

        return null;
    }

    public static String randSelect(HashMap<String, Double> buckets){
        String[] bucketNames= buckets.keySet().toArray(new String[buckets.keySet().size()]);
        Double[] bucketAllocs=buckets.values().toArray(new Double[buckets.values().size()]);
        double currDouble= rand.nextDouble();
        for (int i = 0; i <bucketAllocs.length; i++){
            currDouble-=bucketAllocs[i];
            if (currDouble < 0){
                return bucketNames[i];
            }
        }
        return bucketNames[bucketNames.length-1];

    }

    //this will essentially function as one iteration and will yield a bucket
    public static String ThomsponSample(HashMap<String, Double> buckets){
        ThompsonSampling thompsonSampling = new ThompsonSampling(currExperiment1,buckets.size());
        return thompsonSampling.BucketToSelect;
    }

    public static String greedySelect(HashMap<String, Double> buckets){
        GreedySelection greedySelection = new GreedySelection(currExperiment1, buckets.size());
        return greedySelection.BucketToSelect;
    }


    public static void main(String[] args){
        for (int i = 0; i< 10;i++) {
            System.out.println(doBucketSelection("stochasticRand"));
        }
    }





}
