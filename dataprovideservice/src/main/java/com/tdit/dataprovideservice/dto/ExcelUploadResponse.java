package com.tdit.dataprovideservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ExcelUploadResponse {
    private UUID uploadId;
    private String fileName;
    private Integer totalRows;
    private Integer successRows;
    private Integer failedRows;
    private Integer warningRows;
    private String status;
    private String message;
}
