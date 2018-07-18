package com.hivedata.euclid.hilbert.repo;


import com.hivedata.euclid.hilbert.entity.Bucket;
import com.hivedata.euclid.hilbert.entity.Experiment;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface BucketRepo extends CrudRepository<Bucket, UUID> {

}
