//package com.example.libraryManagementSystem.service;
//
//import com.example.libraryManagementSystem.dto.requestDto.ExportRequest;
//import com.example.libraryManagementSystem.repository.BookRepository;
//import com.example.libraryManagementSystem.repository.FineRepository;
//import com.example.libraryManagementSystem.repository.TransactionRepository;
//import com.example.libraryManagementSystem.repository.UserRepository;
//import jakarta.servlet.http.HttpServletResponse;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.List;
//
//@Service
//public class ExcelExportService {
//
//    @Autowired
//    private BookRepository bookRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private FineRepository fineRepository;
//
//    @Autowired
//    private TransactionRepository transactionRepository;
//
//    // Method to export data based on the ExportRequestDTO
//    public void exportToExcel(ExportRequest exportRequest, HttpServletResponse response) throws IOException {
//        Workbook workbook = new XSSFWorkbook();
//
//        // StringBuilder to accumulate entity names for the file name
//        StringBuilder fileNameBuilder = new StringBuilder("library_data");
//
//        // Conditionally add sheets based on the DTO and update the file name
//        if (exportRequest.isIncludeBooks()) {
//            exportEntityData(bookRepository.findAll(), workbook, "Books");
//            fileNameBuilder.append("_Books");
//        }
//
//        if (exportRequest.isIncludeUsers()) {
//            exportEntityData(userRepository.findAll(), workbook, "Users");
//            fileNameBuilder.append("_Users");
//        }
//
//        if (exportRequest.isIncludeFines()) {
//            exportEntityData(fineRepository.findAll(), workbook, "Fines");
//            fileNameBuilder.append("_Fines");
//        }
//
//        if (exportRequest.isIncludeTransactions()) {
//            exportEntityData(transactionRepository.findAll(), workbook, "Transactions");
//            fileNameBuilder.append("_Transactions");
//        }
//
//        // Convert StringBuilder to String and ensure file name ends with ".xlsx"
//        String fileName = fileNameBuilder.toString() + ".xlsx";
//
//        // Set the content type and header for the Excel file
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
//
//        // Write the workbook to the response output stream
//        workbook.write(response.getOutputStream());
//
//        // Close the workbook
//        workbook.close();
//    }
//
//
//    // Generic method to export data from any entity to Excel
//    private <T> void exportEntityData(List<T> entityList, Workbook workbook, String sheetName) {
//        if (entityList == null || entityList.isEmpty()) {
//            return;
//        }
//
//        Sheet sheet = workbook.createSheet(sheetName);
//        Row headerRow = sheet.createRow(0);
//
//        // Get the first entity and get its fields dynamically to create headers
//        T firstEntity = entityList.get(0);
//        int cellNum = 0;
//
//        for (var field : firstEntity.getClass().getDeclaredFields()) {
//            headerRow.createCell(cellNum++).setCellValue(field.getName());
//        }
//
//        // Write entity data to the sheet
//        int rowNum = 1;
//        for (T entity : entityList) {
//            Row row = sheet.createRow(rowNum++);
//            cellNum = 0;
//            for (var field : entity.getClass().getDeclaredFields()) {
//                field.setAccessible(true); // Allow access to private fields
//                try {
//                    Object value = field.get(entity);
//                    row.createCell(cellNum++).setCellValue(value != null ? value.toString() : "");
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}
//
////------------------------------------------------------------- 2nd Approach ---------------------------------------------------------------
////------------------------------------------------------------------------------------------------------------------------------------------
//
////public void exportToExcel(HttpServletResponse response) throws IOException {
////    // Get data from the database
////    List<Book> books = bookRepository.findAll();
////
////    // Create a new workbook
////    Workbook workbook = new XSSFWorkbook();
////    Sheet sheet = workbook.createSheet("Books");
////
////    // Create header row
////    Row headerRow = sheet.createRow(0);
////    headerRow.createCell(0).setCellValue("Book ID");
////    headerRow.createCell(1).setCellValue("Title");
////    headerRow.createCell(2).setCellValue("Author");
////    headerRow.createCell(3).setCellValue("Publication Year");
////    headerRow.createCell(4).setCellValue("Category");
////    headerRow.createCell(5).setCellValue("Availability Status");
////
////    // Write book data to the Excel file
////    int rowNum = 1;
////    for (Book book : books) {
////        Row row = sheet.createRow(rowNum++);
////        row.createCell(0).setCellValue(book.getBookId());
////        row.createCell(1).setCellValue(book.getTitle());
////        row.createCell(2).setCellValue(book.getAuthor());
////        row.createCell(3).setCellValue(book.getPublicationYear());
////        row.createCell(4).setCellValue(book.getCategory());
////        row.createCell(5).setCellValue(book.isAvailabilityStatus() ? "Available" : "Not Available");
////    }
////
////    // Set the content type and header for the Excel file
////    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
////    response.setHeader("Content-Disposition", "attachment; filename=books.xlsx");
////
////    // Write the workbook to the response output stream
////    workbook.write(response.getOutputStream());
////
////    // Close the workbook
////    workbook.close();
////}