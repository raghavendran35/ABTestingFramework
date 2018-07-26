package com.hivedata.euclid.hilbert.repo;


import com.hivedata.euclid.hilbert.entity.UserManagement;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;


public interface UserManagementRepo extends CrudRepository<UserManagement, UUID> {

}
