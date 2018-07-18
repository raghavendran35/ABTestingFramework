package com.hivedata.euclid.hilbert.repo;


import com.hivedata.euclid.hilbert.entity.User;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;


public interface UserRepo extends CrudRepository<User, UUID> {

}
