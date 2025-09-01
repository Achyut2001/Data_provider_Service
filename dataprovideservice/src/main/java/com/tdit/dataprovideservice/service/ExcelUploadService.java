package com.tdit.dataprovideservice.service;

import com.tdit.dataprovideservice.dto.ExcelRowData;
import com.tdit.dataprovideservice.dto.ExcelUploadResponse;
import com.tdit.dataprovideservice.entity.Property;
import com.tdit.dataprovideservice.entity.UploadAudit;
import com.tdit.dataprovideservice.repository.PropertyRepository;
import com.tdit.dataprovideservice.repository.UploadAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelUploadService {

    private final ExcelProcessorService excelProcessorService;
    private final ExcelValidationService excelValidationService;
    private final PropertyRepository propertyRepository;
    private final UploadAuditRepository uploadAuditRepository;

    @Transactional
    public ExcelUploadResponse processExcelUpload(MultipartFile file, String uploadedBy) {

        UploadAudit audit = UploadAudit.builder()
                .fileName(file.getOriginalFilename())
                .uploadedBy(uploadedBy)
                .timestamp(LocalDateTime.now())
                .status(UploadAudit.UploadStatus.PROCESSING)
                .rowResults(new HashMap<>())
                .build();

        // Save to DB so Hibernate generates UUID
        audit = uploadAuditRepository.save(audit);
        UUID uploadId = audit.getUploadId();

        try {
            // Process Excel file
            List<ExcelRowData> rows = excelProcessorService.processExcelFile(file);

            int totalRows = rows.size();
            int successRows = 0;
            int failedRows = 0;
            int warningRows = 0;

            Map<Integer, UploadAudit.RowResult> rowResults = new HashMap<>();
            List<Property> validProperties = new ArrayList<>();

            // Validate each row
            for (int i = 0; i < rows.size(); i++) {
                ExcelRowData rowData = rows.get(i);
                int rowNumber = i + 2; // Excel rows start from 1, skip header

                if (rowData == null) {
                    UploadAudit.RowResult result = UploadAudit.RowResult.builder()
                            .success(false)
                            .errorMessage("Failed to extract row data")
                            .rowNumber(rowNumber)
                            .build();
                    rowResults.put(rowNumber, result);
                    failedRows++;
                    continue;
                }

                UploadAudit.RowResult validationResult = excelValidationService.validateRow(rowData, rowNumber);
                rowResults.put(rowNumber, validationResult);

                if (validationResult.isSuccess()) {
                    try {
                        Property property = excelProcessorService.convertToProperty(rowData);
                        validProperties.add(property);
                        successRows++;

                        if (validationResult.getWarningMessage() != null) {
                            warningRows++;
                        }
                    } catch (Exception e) {
                        log.error("Error converting row {} to property: {}", rowNumber, e.getMessage());
                        validationResult.setSuccess(false);
                        validationResult.setErrorMessage("Error converting to property: " + e.getMessage());
                        successRows--;
                        failedRows++;
                    }
                } else {
                    failedRows++;
                }
            }

            // Save valid properties to database
            if (!validProperties.isEmpty()) {
                propertyRepository.saveAll(validProperties);
            }

            // Update audit record
            audit.setStatus(UploadAudit.UploadStatus.COMPLETED);
            audit.setRowResults(rowResults);
            audit.setTotalRows(totalRows);
            audit.setSuccessRows(successRows);
            audit.setFailedRows(failedRows);
            audit.setWarningRows(warningRows);

            uploadAuditRepository.save(audit);

            return ExcelUploadResponse.builder()
                    .uploadId(uploadId)
                    .fileName(file.getOriginalFilename())
                    .totalRows(totalRows)
                    .successRows(successRows)
                    .failedRows(failedRows)
                    .warningRows(warningRows)
                    .status("COMPLETED")
                    .message(String.format("Processed %d rows: %d success, %d failed, %d warnings",
                            totalRows, successRows, failedRows, warningRows))
                    .build();

        } catch (Exception e) {
            log.error("Error processing Excel upload: {}", e.getMessage(), e);

            // Update audit record with failure
            audit.setStatus(UploadAudit.UploadStatus.FAILED);
            uploadAuditRepository.save(audit);

            throw new RuntimeException("Failed to process Excel file: " + e.getMessage());
        }
    }

    public UploadAudit getUploadStatus(UUID uploadId) {
        return uploadAuditRepository.findById(uploadId)
                .orElseThrow(() -> new RuntimeException("Upload not found: " + uploadId));
    }
}
