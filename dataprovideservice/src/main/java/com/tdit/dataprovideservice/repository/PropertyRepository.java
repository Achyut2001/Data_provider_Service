package com.tdit.dataprovideservice.repository;

import com.tdit.dataprovideservice.entity.Property;
import com.tdit.dataprovideservice.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByStatus(String status);
}
