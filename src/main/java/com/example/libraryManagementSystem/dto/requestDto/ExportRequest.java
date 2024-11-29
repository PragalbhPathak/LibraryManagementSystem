package com.example.libraryManagementSystem.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportRequest {

    private boolean includeBooks;
    private boolean includeUsers;
    private boolean includeFines;
    private boolean includeTransactions;

}
