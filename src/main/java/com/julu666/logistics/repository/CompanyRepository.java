package com.julu666.logistics.repository;

import com.julu666.logistics.jpa.Company;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CompanyRepository extends CrudRepository <Company, Long> {
    Optional<Company> findByCompanyName(String name);
}
