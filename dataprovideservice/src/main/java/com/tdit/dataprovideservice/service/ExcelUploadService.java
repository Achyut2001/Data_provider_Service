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

import static com.tdit.dataprovideservice.entity.Constants.*;

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

        audit = uploadAuditRepository.save(audit);
        UUID uploadId = audit.getUploadId();

        try {
            List<ExcelRowData> rows = excelProcessorService.processExcelFile(file);

            int totalRows = rows.size();
            int successRows = 0;
            int failedRows = 0;
            int warningRows = 0;

            Map<Integer, UploadAudit.RowResult> rowResults = new HashMap<>();
            List<Property> validProperties = new ArrayList<>();

            for (int i = 0; i < rows.size(); i++) {
                ExcelRowData rowData = rows.get(i);
                int rowNumber = i + 2;

                if (rowData == null) {
                    UploadAudit.RowResult result = UploadAudit.RowResult.builder()
                            .success(false)
                            .errorMessage(ERROR_ROW_EXTRACTION)
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
                        log.error(ERROR_PROPERTY_CONVERSION, rowNumber, e.getMessage());
                        validationResult.setSuccess(false);
                        validationResult.setErrorMessage(ERROR_PROPERTY_CONVERSION + e.getMessage());
                        successRows--;
                        failedRows++;
                    }
                } else {
                    failedRows++;
                }
            }

            if (!validProperties.isEmpty()) {
                propertyRepository.saveAll(validProperties);
            }

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
                    .status(STATUS_COMPLETED)
                    .message(String.format(MESSAGE_TEMPLATE,
                            totalRows, successRows, failedRows, warningRows))
                    .build();

        } catch (Exception e) {
            log.error(ERROR_PROCESSING_EXCEL_FILE, e.getMessage(), e);

            audit.setStatus(UploadAudit.UploadStatus.FAILED);
            uploadAuditRepository.save(audit);

            throw new RuntimeException(ERROR_PROCESSING_EXCEL_FILE + e.getMessage());
        }
    }

    public UploadAudit getUploadStatus(UUID uploadId) {
        return uploadAuditRepository.findById(uploadId)
                .orElseThrow(() -> new RuntimeException(UPLOAD_NOT_FOUND + uploadId));
    }
}
