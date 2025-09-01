package com.tdit.dataprovideservice.controller;

import com.tdit.dataprovideservice.dto.ExcelUploadResponse;
import com.tdit.dataprovideservice.entity.UploadAudit;
import com.tdit.dataprovideservice.service.ExcelUploadService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
@Slf4j
@Builder
@CrossOrigin(origins = "*")
public class ExcelUploadController {

    private final ExcelUploadService excelUploadService;

    @PostMapping("/upload")
    public ResponseEntity<ExcelUploadResponse> uploadExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "uploadedBy", defaultValue = "system") String uploadedBy) {
        
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ExcelUploadResponse.builder()
                                .status("FAILED")
                                .message("File is empty")
                                .build());
            }
            
            if (!file.getOriginalFilename().endsWith(".xlsx")) {
                return ResponseEntity.badRequest()
                        .body(ExcelUploadResponse.builder()
                                .status("FAILED")
                                .message("Only .xlsx files are supported")
                                .build());
            }
            
            log.info("Processing Excel upload: {} by user: {}", file.getOriginalFilename(), uploadedBy);
            
            ExcelUploadResponse response = excelUploadService.processExcelUpload(file, uploadedBy);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error in Excel upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ExcelUploadResponse.builder()
                            .status("FAILED")
                            .message("Error processing file: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/status/{uploadId}")
    public ResponseEntity<UploadAudit> getUploadStatus(@PathVariable UUID uploadId) {
        try {
            UploadAudit audit = excelUploadService.getUploadStatus(uploadId);
            return ResponseEntity.ok(audit);
        } catch (Exception e) {
            log.error("Error getting upload status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }


}
