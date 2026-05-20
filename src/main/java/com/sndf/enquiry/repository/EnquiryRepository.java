package com.sndf.enquiry.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sndf.enquiry.entity.Enquiry;

public interface EnquiryRepository
        extends JpaRepository<Enquiry, Long> {

    List<Enquiry> findBySourceWebsite(
            String sourceWebsite);

    List<Enquiry> findByDepartment(
            String department);
}



