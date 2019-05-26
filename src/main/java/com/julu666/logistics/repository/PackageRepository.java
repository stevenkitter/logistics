package com.julu666.logistics.repository;

import com.julu666.logistics.jpa.Package;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PackageRepository extends CrudRepository<Package, Long> {
    Optional<Package> findByMailNo(String mailNo);
}
