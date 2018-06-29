package com.sample.cassandra.repo;


import com.sample.cassandra.entity.Bucket;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface BucketRepo extends CrudRepository<Bucket, UUID> {

}
