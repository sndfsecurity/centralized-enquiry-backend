package com.sndf.enquiry.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sndf.enquiry.entity.Enquiry;
import com.sndf.enquiry.repository.EnquiryRepository;

import org.springframework.data.domain.Sort;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.io.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.format.DateTimeFormatter;

@Service
public class EnquiryService {

    @Autowired
    private EnquiryRepository enquiryRepository;
    
    public Enquiry saveEnquiry(Enquiry enquiry) {

        enquiry.setMobileNumber(
                enquiry.getMobileNumber().trim());

        enquiry.setService(
                enquiry.getService().trim());

        LocalDateTime twentyFourHoursAgo =
                LocalDateTime.now().minusHours(24);

        List<Enquiry> duplicateEnquiries =
                enquiryRepository.findDuplicateEnquiry(
                        enquiry.getMobileNumber(),
                        enquiry.getService(),
                        twentyFourHoursAgo);

        if (!duplicateEnquiries.isEmpty()) {

            throw new RuntimeException(
                    "You have already submitted an enquiry for this service within the last 24 hours.");
        }

        String website =
                enquiry.getSourceWebsite();

        website = website.replace("www.", "");

        enquiry.setDepartment(website);

        return enquiryRepository.save(enquiry);
    }
    
   
    public List<Enquiry> getAllEnquiries() {

        return enquiryRepository.findAll(
                Sort.by(Sort.Direction.DESC, "id"));
    }
    
    
    
    public Enquiry updateStatus(
            Long id,
            String status) {

        Enquiry enquiry =
                enquiryRepository.findById(id)
                .orElseThrow();

        String currentStatus =
                enquiry.getStatus();

        if ("COMPLETED".equals(currentStatus)) {

            throw new RuntimeException(
                    "Completed enquiry cannot be modified");
        }

        if ("NEW".equals(currentStatus)
                && !"IN_PROGRESS".equals(status)) {

            throw new RuntimeException(
                    "NEW enquiry can only move to IN_PROGRESS");
        }

        if ("IN_PROGRESS".equals(currentStatus)
                && !"COMPLETED".equals(status)) {

            throw new RuntimeException(
                    "IN_PROGRESS enquiry can only move to COMPLETED");
        }

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
        
    
    public Page<Enquiry> getMyEnquiries(
            String role,
            String department,
            String filterDepartment,
            String search,
            String dateFilter,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "id"));

        LocalDateTime filterDate = null;

        if ("TODAY".equals(dateFilter)) {

            filterDate =
                    LocalDate.now()
                             .atStartOfDay();
        }
        else if ("LAST_7_DAYS".equals(dateFilter)) {

            filterDate =
                    LocalDateTime.now()
                            .minusDays(7);
        }
        else if ("LAST_30_DAYS".equals(dateFilter)) {

            filterDate =
                    LocalDateTime.now()
                            .minusDays(30);
        }

        // SUPER ADMIN

        if (role.equals("SUPER_ADMIN")) {

            if (filterDate != null) {

                return enquiryRepository
                        .findByCreatedAtAfter(
                                filterDate,
                                pageable);
            }

            // Department + Search

            if (filterDepartment != null
                    && !filterDepartment.isBlank()
                    && search != null
                    && !search.isBlank()) {

                return enquiryRepository
                        .searchByDepartment(
                                filterDepartment,
                                search,
                                pageable);
            }

            // Department Only

            if (filterDepartment != null
                    && !filterDepartment.isBlank()) {

                return enquiryRepository
                        .findByDepartment(
                                filterDepartment,
                                pageable);
            }

            // Search Only

            if (search != null
                    && !search.isBlank()) {

                return enquiryRepository
                        .searchAll(
                                search,
                                pageable);
            }

            // No Filters

            return enquiryRepository.findAll(
                    pageable);
        }

        // DEPARTMENT USER

        if (filterDate != null) {

            return enquiryRepository
                    .findByDepartmentAndCreatedAtAfter(
                            department,
                            filterDate,
                            pageable);
        }

        if (search != null
                && !search.isBlank()) {

            return enquiryRepository
                    .searchByDepartment(
                            department,
                            search,
                            pageable);
        }

        return enquiryRepository.findByDepartment(
                department,
                pageable);
    }
       

     
    
    
    public void deleteEnquiry(Long id) {

        enquiryRepository.deleteById(id);
    }
    
    
    //pagination
    
    public Page<Enquiry> getMyEnquiries(
            String role,
            String department,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "id"));

        if (role.equals("SUPER_ADMIN")) {

            return enquiryRepository.findAll(
                    pageable);
        }

        return enquiryRepository.findByDepartment(
                department,
                pageable);
    }
    
    
    
    // fetch count 
    
    public long getTotalEnquiries() {

        return enquiryRepository.count();
    }

    public long getNewEnquiries() {

        return enquiryRepository.countByStatus("NEW");
    }

    public long getDepartmentsCount() {

        return enquiryRepository.countDepartments();
    }
    
    
    public long getTotalEnquiriesByDepartment(
            String department) {

        return enquiryRepository
                .countByDepartment(department);
    }

    public long getNewEnquiriesByDepartment(
            String department) {

        return enquiryRepository
                .countByDepartmentAndStatus(
                        department,
                        "NEW");
    }

    public long getDepartmentsCountByRole(
            String role) {

        if (role.equals("SUPER_ADMIN")) {
            return enquiryRepository.countDepartments();
        }

        return 1;
    }
    
    
    // export to excel
    public byte[] exportToExcel(
            String role,
            String department,
            String dateFilter) {

        try {

            List<Enquiry> enquiries;

            LocalDateTime filterDate = null;

            if ("TODAY".equals(dateFilter)) {

                filterDate =
                        LocalDate.now()
                                 .atStartOfDay();
            }
            else if ("LAST_7_DAYS".equals(dateFilter)) {

                filterDate =
                        LocalDateTime.now()
                                .minusDays(7);
            }
            else if ("LAST_30_DAYS".equals(dateFilter)) {

                filterDate =
                        LocalDateTime.now()
                                .minusDays(30);
            }

            if (role.equals("SUPER_ADMIN")) {

                if (filterDate != null) {

                    enquiries =
                            enquiryRepository
                                    .findByCreatedAtAfter(
                                            filterDate);

                } else {

                    enquiries =
                            enquiryRepository
                                    .findAll();
                }

            } else {

                if (filterDate != null) {

                    enquiries =
                            enquiryRepository
                                    .findByDepartmentAndCreatedAtAfter(
                                            department,
                                            filterDate);

                } else {

                    enquiries =
                            enquiryRepository
                                    .findByDepartment(
                                            department);
                }
            }

            Workbook workbook =
                    new XSSFWorkbook();

            Sheet sheet =
                    workbook.createSheet(
                            "Enquiries");

            Row header =
                    sheet.createRow(0);

            header.createCell(0)
                  .setCellValue("Name");

            header.createCell(1)
                  .setCellValue("Mobile");

            header.createCell(2)
                  .setCellValue("City");

            header.createCell(3)
                  .setCellValue("Service");
            
            header.createCell(4)
               .setCellValue("Date & Time");


            header.createCell(5)
                  .setCellValue("Status");
            
            
            int rowNum = 1;
            
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern(
                            "d/M/yyyy, h:mm:ss a");

            for (Enquiry enquiry : enquiries) {

                Row row =
                        sheet.createRow(
                                rowNum++);

                row.createCell(0)
                   .setCellValue(
                           enquiry.getFullName());

                row.createCell(1)
                   .setCellValue(
                           enquiry.getMobileNumber());

                row.createCell(2)
                   .setCellValue(
                           enquiry.getCity());

                row.createCell(3)
                   .setCellValue(
                           enquiry.getService());

                row.createCell(4)
                .setCellValue(
                        enquiry.getCreatedAt()
                               .format(formatter)
                               .toLowerCase());

             row.createCell(5)
                .setCellValue(
                        enquiry.getStatus());
            }
            
             
//            sheet.autoSizeColumn(0);
//            sheet.autoSizeColumn(1);
//            sheet.autoSizeColumn(2);
//            sheet.autoSizeColumn(3);
//            sheet.autoSizeColumn(4);
//            sheet.autoSizeColumn(5);

            
            sheet.setColumnWidth(0, 6000);
            sheet.setColumnWidth(1, 5000);
            sheet.setColumnWidth(2, 5000);
            sheet.setColumnWidth(3, 6000);
            sheet.setColumnWidth(4, 7000);
            sheet.setColumnWidth(5, 7000);
            
            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            workbook.write(out);

            workbook.close();

            return out.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Excel Export Failed");
        }
    }
    
    
    
}