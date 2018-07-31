package com.hivedata.euclid.hilbert.controller;
import java.io.IOException;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.*;

public class Objectify {
    //take current cassandra data and objectify
    Cluster cluster = Cluster.builder().withClusterName("currentCluster").
                                addContactPoint("127.0.0.1").build();
    ResultSet resultSet;
    ExperimentList experimentList = new ExperimentList();

    public Objectify(String currExperiment){
        Session session = cluster.connect();
        session.execute("USE HILBERT;");
        this.resultSet = session.execute("select * from Analytics where experiment_id="+currExperiment);
        for (Row row: resultSet){
            this.experimentList.experiments.add(new Experiment(row.getString(0), row.getString(3),
                                                                row.getString(1), row.getInt(2)));
        }
        session.close();



    }

    public static void main(String[] args) throws IOException {
        Objectify obj = new Objectify("'buttonTest'");
        System.out.println(obj.experimentList.experiments.get(1).getBucket_ID());
        System.out.println(obj.experimentList.experiments.get(1).getSession_Token());
        System.out.println(obj.experimentList.experiments.get(1).getSelected());
        System.out.println(obj.experimentList.experiments.get(1).getExperiment_ID());


    }
}
