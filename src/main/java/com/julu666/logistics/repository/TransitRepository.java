package com.julu666.logistics.repository;

import com.julu666.logistics.jpa.Transit;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TransitRepository extends CrudRepository<Transit, Long> {
    Optional<Transit> findByTime(String time);
}
