package com.example.libraryManagementSystem.restController;

import com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT;
import com.example.libraryManagementSystem.dto.requestDto.ExportRequest;
import com.example.libraryManagementSystem.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.BASE_URL;


@RestController
@RequestMapping(BASE_URL)
public class ExportController {

    @Autowired
    private ExportService exportService;

    // Endpoint to trigger the Excel export via POST request with DTO
//    @PostMapping("/export/library-data")
//    public void exportLibraryData(@RequestBody ExportRequest exportRequest, HttpServletResponse response) throws IOException {
//        excelExportService.exportToExcel(exportRequest, response);
//    }

    // Endpoint to trigger the Excel export via POST request with DTO
    @PreAuthorize("hasRole('Librarian')")
    @PostMapping(REST_MAPPING_CONSTRAINT.DEFINED_API.EXPORT_DATA)
    public void exportLibraryData(@RequestBody ExportRequest exportRequest, HttpServletResponse response) throws IOException {
        exportService.exportToZip(exportRequest, response); // Use exportToZip method for ZIP download
    }
}
