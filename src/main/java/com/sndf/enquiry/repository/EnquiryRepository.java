package com.sndf.enquiry.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sndf.enquiry.entity.Enquiry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;


public interface EnquiryRepository
        extends JpaRepository<Enquiry, Long> {

    List<Enquiry> findBySourceWebsite(
            String sourceWebsite);

    List<Enquiry> findByDepartment(
            String department);
    
    Page<Enquiry> findByDepartment(
            String department,
            Pageable pageable);
    
    
    
 // Search for Super Admin

    @Query("""
           SELECT e
           FROM Enquiry e
           WHERE LOWER(e.fullName)
                 LIKE LOWER(CONCAT('%', :search, '%'))
              OR e.mobileNumber
                 LIKE CONCAT('%', :search, '%')
           """)
    Page<Enquiry> searchAll(
            @Param("search") String search,
            Pageable pageable);
       
    
 // Search for Department Users

    @Query("""
           SELECT e
           FROM Enquiry e
           WHERE e.department = :department
           AND (
                LOWER(e.fullName)
                LIKE LOWER(CONCAT('%', :search, '%'))
                OR e.mobileNumber
                LIKE CONCAT('%', :search, '%')
           )
           """)
    Page<Enquiry> searchByDepartment(
            @Param("department") String department,
            @Param("search") String search,
            Pageable pageable);
    
    
    
    long countByStatus(String status);

    @Query("SELECT COUNT(DISTINCT e.department) FROM Enquiry e")
    long countDepartments();
    
    
    long countByDepartment(
            String department);

    long countByDepartmentAndStatus(
            String department,
            String status);
    
    
    Page<Enquiry> findByCreatedAtAfter(
            LocalDateTime date,
            Pageable pageable);

    Page<Enquiry> findByDepartmentAndCreatedAtAfter(
            String department,
            LocalDateTime date,
            Pageable pageable);
    
    
    // for excel export
    
    List<Enquiry> findByCreatedAtAfter(
            LocalDateTime date);

    List<Enquiry> findByDepartmentAndCreatedAtAfter(
            String department,
            LocalDateTime date);
    
    
    @Query("""
    	       SELECT e
    	       FROM Enquiry e
    	       WHERE e.mobileNumber = :mobileNumber
    	       AND LOWER(e.service) = LOWER(:service)
    	       AND e.createdAt >= :timeLimit
    	       """)
    	List<Enquiry> findDuplicateEnquiry(
    	        @Param("mobileNumber") String mobileNumber,
    	        @Param("service") String service,
    	        @Param("timeLimit") LocalDateTime timeLimit);
   
    
}



