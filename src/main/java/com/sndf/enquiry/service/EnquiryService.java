package com.sndf.enquiry.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sndf.enquiry.entity.Enquiry;
import com.sndf.enquiry.repository.EnquiryRepository;

@Service
public class EnquiryService {

    @Autowired
    private EnquiryRepository enquiryRepository;
    

    public Enquiry saveEnquiry(Enquiry enquiry) {

        enquiry.setDepartment(
                enquiry.getSourceWebsite());

        return enquiryRepository.save(enquiry);
    }
    
    
    
    public List<Enquiry> getAllEnquiries() {

        return enquiryRepository.findAll();
    }
    
    
    
    public Enquiry updateStatus(
            Long id,
            String status) {

        Enquiry enquiry =
                enquiryRepository.findById(id)
                .orElseThrow();

        enquiry.setStatus(status);

        return enquiryRepository.save(enquiry);
    }
    
    
    
    
    public List<Enquiry> getEnquiriesByWebsite(
            String website) {

        return enquiryRepository
                .findBySourceWebsite(website);
    }
    
    
    
    public List<Enquiry> getEnquiriesByDepartment(
            String department) {

        return enquiryRepository
                .findByDepartment(department);
    }
    
    
    
    public List<Enquiry> getMyEnquiries(
            String role,
            String department) {

        if (role.equals("SUPER_ADMIN")) {

            return enquiryRepository.findAll();
        }

        return enquiryRepository
                .findByDepartment(department);
    }
    
    
}