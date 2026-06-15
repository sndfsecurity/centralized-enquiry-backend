package com.sndf.enquiry.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.sndf.enquiry.dto.StatusUpdateRequest;
import com.sndf.enquiry.entity.Enquiry;
import com.sndf.enquiry.service.EnquiryService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/enquiry")
@CrossOrigin("*")
public class EnquiryController {

    @Autowired
    private EnquiryService enquiryService;

//    @PostMapping
//    public Enquiry saveEnquiry(
//            @RequestBody Enquiry enquiry) {
//
//        return enquiryService.saveEnquiry(enquiry);
//    }
    
    
    @PostMapping
    public ResponseEntity<?> saveEnquiry(
            @RequestBody Enquiry enquiry) {

        try {

            return ResponseEntity.ok(
                    enquiryService.saveEnquiry(
                            enquiry));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
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
    public Page<Enquiry> getMyEnquiries(

            HttpServletRequest request,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size,

            @RequestParam(required = false)
            String filterDepartment,
            
           
            @RequestParam(defaultValue = "")
            String search,
    
		    @RequestParam(defaultValue = "")
		    String dateFilter) {
    
    
        String role =
                request.getAttribute("role")
                        .toString();

        String department =
                request.getAttribute("department")
                        .toString();
        
        return enquiryService.getMyEnquiries(
                role,
                department,
                filterDepartment,
                search,
                dateFilter,
                page,
                size);
    }
    
    
    @GetMapping("/dashboard-stats")
    public Map<String, Long> getDashboardStats(
            HttpServletRequest request) {

        String role =
                request.getAttribute("role")
                       .toString();

        String department =
                request.getAttribute("department")
                       .toString();

        Map<String, Long> stats =
                new HashMap<>();

        if (role.equals("SUPER_ADMIN")) {

            stats.put(
                    "totalEnquiries",
                    enquiryService.getTotalEnquiries());

            stats.put(
                    "newEnquiries",
                    enquiryService.getNewEnquiries());

            stats.put(
                    "departments",
                    enquiryService.getDepartmentsCount());

        } else {

            stats.put(
                    "totalEnquiries",
                    enquiryService
                            .getTotalEnquiriesByDepartment(
                                    department));

            stats.put(
                    "newEnquiries",
                    enquiryService
                            .getNewEnquiriesByDepartment(
                                    department));

            stats.put(
                    "departments",
                    1L);
        }

        return stats;
    }
    
    
    // excel export ...........................
    
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(

            HttpServletRequest request,

            @RequestParam(defaultValue = "")
            String dateFilter) {

        String role =
                request.getAttribute("role")
                       .toString();

        String department =
                request.getAttribute("department")
                       .toString();

        byte[] excelData =
                enquiryService.exportToExcel(
                        role,
                        department,
                        dateFilter);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=enquiries.xlsx")
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }
    
    
    @DeleteMapping("/{id}")
    public void deleteEnquiry(
            @PathVariable Long id) {

        enquiryService.deleteEnquiry(id);
    }
}