package com.sndf.enquiry.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sndf.enquiry.dto.StatusUpdateRequest;
import com.sndf.enquiry.entity.Enquiry;
import com.sndf.enquiry.service.EnquiryService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/enquiry")
@CrossOrigin("*")
public class EnquiryController {

    @Autowired
    private EnquiryService enquiryService;

    @PostMapping
    public Enquiry saveEnquiry(
            @RequestBody Enquiry enquiry) {

        return enquiryService.saveEnquiry(enquiry);
    }
    

    @GetMapping
    public List<Enquiry> getAllEnquiries() {

        return enquiryService.getAllEnquiries();
    }
    
    
    @GetMapping("/website/{website}")
    public List<Enquiry> getByWebsite(
            @PathVariable String website) {

        return enquiryService
                .getEnquiriesByWebsite(website);
    }
    
    
    @PutMapping("/{id}/status")
    public Enquiry updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {

        return enquiryService
                .updateStatus(id, request.getStatus());
    }
    
    
    
    @GetMapping("/department/{department}")
    public List<Enquiry> getByDepartment(
            @PathVariable String department) {

        return enquiryService
                .getEnquiriesByDepartment(
                        department);
    }
    
    
    
    @GetMapping("/my-enquiries")
    public List<Enquiry> getMyEnquiries(
            HttpServletRequest request) {

        String role =
                request.getAttribute("role")
                .toString();

        String department =
                request.getAttribute("department")
                .toString();

        return enquiryService.getMyEnquiries(
                role,
                department);
    }
}