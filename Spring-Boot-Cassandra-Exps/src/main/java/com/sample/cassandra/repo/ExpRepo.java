package com.sample.cassandra.repo;


import com.sample.cassandra.entity.Experiment;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ExpRepo extends CrudRepository<Experiment, UUID> {


    Experiment findById(UUID id);
}
