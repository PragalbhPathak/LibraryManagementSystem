package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.dto.requestDto.ExportRequest;
import com.example.libraryManagementSystem.repository.BookRepository;
import com.example.libraryManagementSystem.repository.FineRepository;
import com.example.libraryManagementSystem.repository.TransactionRepository;
import com.example.libraryManagementSystem.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ExportService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // Method to export data based on the ExportRequestDTO
    public void exportToZip(ExportRequest exportRequest, HttpServletResponse response) throws IOException {

        // Check if all fields are false
        if (!exportRequest.isIncludeBooks() && !exportRequest.isIncludeUsers() && !exportRequest.isIncludeFines() && !exportRequest.isIncludeTransactions()) {
            // If no field is selected, return an error response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            response.getWriter().write("Error: No fields selected for export. Please select at least one field.");
            response.getWriter().flush();
            return;
        }

        // Create a ByteArrayOutputStream to hold the ZIP data
        ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(zipOut);

        // Conditionally add sheets based on the DTO and update the file name
        if (exportRequest.isIncludeBooks()) {
            addEntityToZip(bookRepository.findAll(), zos, "Books.xlsx");
        }
        if (exportRequest.isIncludeUsers()) {
            addEntityToZip(userRepository.findAll(), zos, "Users.xlsx");
        }
        if (exportRequest.isIncludeFines()) {
            addEntityToZip(fineRepository.findAll(), zos, "Fines.xlsx");
        }
        if (exportRequest.isIncludeTransactions()) {
            addEntityToZip(transactionRepository.findAll(), zos, "Transactions.xlsx");
        }

        // Close the ZIP output stream
        zos.close();

        // Set the content type and header for the ZIP file
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=library_data.zip");

        // Write the ZIP data to the response output stream
        FileCopyUtils.copy(zipOut.toByteArray(), response.getOutputStream());

        // Close the streams
        zipOut.close();
    }

    // Method to add individual Excel files for each entity to the ZIP
    private <T> void addEntityToZip(List<T> entityList, ZipOutputStream zos, String fileName) throws IOException {
        if (entityList == null || entityList.isEmpty()) {
            return;
        }

        // Create the Excel workbook for the entity
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(fileName.replace(".xlsx", ""));

        // Create the header row for the sheet
        Row headerRow = sheet.createRow(0);
        T firstEntity = entityList.get(0);
        int cellNum = 0;
        for (var field : firstEntity.getClass().getDeclaredFields()) {
            headerRow.createCell(cellNum++).setCellValue(field.getName());
        }

        // Write entity data to the sheet
        int rowNum = 1;
        for (T entity : entityList) {
            Row row = sheet.createRow(rowNum++);
            cellNum = 0;
            for (var field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true); // Allow access to private fields
                try {
                    Object value = field.get(entity);
                    row.createCell(cellNum++).setCellValue(value != null ? value.toString() : "");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        // Auto-size all columns based on the content
        for (int i = 0; i < firstEntity.getClass().getDeclaredFields().length; i++) {
            sheet.autoSizeColumn(i); // Auto-size each column based on the data
        }

        // Write the workbook to a ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();

        // Create a new ZipEntry for the file and write the byte array to the ZIP output stream
        ZipEntry zipEntry = new ZipEntry(fileName);
        zos.putNextEntry(zipEntry);
        zos.write(byteArrayOutputStream.toByteArray());
        zos.closeEntry();
    }

}
