package com.sample.cassandra.repo;


import com.sample.cassandra.entity.User;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepo extends CrudRepository<User, UUID> {


    User findById(UUID id);
}
