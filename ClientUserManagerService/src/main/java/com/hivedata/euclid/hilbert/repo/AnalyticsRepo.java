package com.hivedata.euclid.hilbert.repo;


import com.hivedata.euclid.hilbert.entity.Analytics;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;


public interface AnalyticsRepo extends CrudRepository<Analytics, UUID> {

}
