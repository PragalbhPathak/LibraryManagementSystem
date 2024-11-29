    package com.example.libraryManagementSystem.dto.requestDto;

    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class SearchRequest {
        private Long id;
        private boolean findAll = true;
    }
