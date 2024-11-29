package com.example.libraryManagementSystem.constraint;

public interface REST_MAPPING_CONSTRAINT {
    String BASE_URL = "/api/user";

    interface DEFINED_API{

        String EXPORT_DATA = "/export/library-data";
        String LOGIN_USER = "/userLogin";

        String REGISTER_USER = "/registerUser";
        String GET_USER = "/getUser";

        String UPDATE_BOOK = "/updateBook";
        String GET_BOOK = "/getBook";

        String UPDATE_TRANSACTION = "/updateTransaction";
        String GET_TRANSACTION = "/getTransaction";

        String UPDATE_FINE = "/updateFine";
        String GET_FINE = "/getFine";

    }
    interface STATUS_CODES{
        String INTERNAL_SERVER_ERROR="500";
        String SUCCESS_OK="200";
        String SUCCESSFULLY_CREATED = "201";
        String  PARTIAL_STATUS="207";
        String BAD_REQUEST= "400";
        String UNAUTHORIZED = "401";
        String FORBIDDEN = "403";
        String NOT_FOUND ="404";
    }
    interface SUCCESS_STATUS{
        int SUCCESS = 1;
        int FAILURE = 0;
    }

    interface COMMON_MESSAGE{
        String MESSAGE_CREATE = "Created Successfully";
        String MESSAGE_UPDATE = "Update Successfully";
        String MESSAGE_SAVE = "Data Saved Successfully";
        String MESSAGE_INVALID_CREDENTIALS = "Invalid Credentials";
        String MESSAGE_COMMON_SERVER_ERROR = "Something went wrong";
        String MESSAGE_NOT_FOUND = "Data not found";
        String MESSAGE_FIELD_REQUIRED = "Field is required";
        String MESSAGE_FETCH_DATA = "Data fetched Successfully";
        String MESSAGE_UNAUTHORIZED = "Access Denied";

    }

}
