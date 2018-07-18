package com.hivedata.euclid.hilbert.repo;


import com.hivedata.euclid.hilbert.entity.Experiment;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ExpRepo extends CrudRepository<Experiment, UUID> {

}
