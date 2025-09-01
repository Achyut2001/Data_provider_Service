package com.tdit.dataprovideservice.service;

import com.tdit.dataprovideservice.dto.ExcelRowData;
import com.tdit.dataprovideservice.entity.UploadAudit;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExcelValidationService {

    private final Validator validator;

    public UploadAudit.RowResult validateRow(ExcelRowData rowData, int rowNumber) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Use Spring's validation framework
        Set<ConstraintViolation<ExcelRowData>> violations = validator.validate(rowData);
        
        for (ConstraintViolation<ExcelRowData> violation : violations) {
            errors.add(violation.getMessage());
        }

        // Additional business logic validations that can't be expressed with annotations
        addBusinessValidations(rowData, errors, warnings);

        boolean success = errors.isEmpty();
        
        return UploadAudit.RowResult.builder()
                .success(success)
                .errorMessage(errors.isEmpty() ? null : String.join("; ", errors))
                .warningMessage(warnings.isEmpty() ? null : String.join("; ", warnings))
                .rowNumber(rowNumber)
                .build();
    }

    private void addBusinessValidations(ExcelRowData rowData, List<String> errors, List<String> warnings) {
        // Currency validation - check if it's in our preferred list
        if (rowData.getCurrency() != null) {
            List<String> preferredCurrencies = List.of("INR", "USD", "EUR", "GBP", "CAD", "AUD");
            if (!preferredCurrencies.contains(rowData.getCurrency().toUpperCase())) {
                warnings.add("Currency " + rowData.getCurrency() + " is not in preferred list");
            }
        }

        // Latitude validation - check range
        if (rowData.getLatitude() != null && !rowData.getLatitude().trim().isEmpty()) {
            try {
                double lat = Double.parseDouble(rowData.getLatitude());
                if (lat < -90 || lat > 90) {
                    errors.add("Latitude must be between -90 and 90");
                }
            } catch (NumberFormatException e) {
                errors.add("Latitude must be a valid number");
            }
        }

        // Longitude validation - check range
        if (rowData.getLongitude() != null && !rowData.getLongitude().trim().isEmpty()) {
            try {
                double lng = Double.parseDouble(rowData.getLongitude());
                if (lng < -180 || lng > 180) {
                    errors.add("Longitude must be between -180 and 180");
                }
            } catch (NumberFormatException e) {
                errors.add("Longitude must be a valid number");
            }
        }

        // Base price validation - check if positive
        if (rowData.getBasePrice() != null && !rowData.getBasePrice().trim().isEmpty()) {
            try {
                double price = Double.parseDouble(rowData.getBasePrice());
                if (price <= 0) {
                    errors.add("Base price must be greater than 0");
                }
            } catch (NumberFormatException e) {
                errors.add("Base price must be a valid number");
            }
        }

        // Property URL validation - additional check for common TLDs
        if (rowData.getPropertyUrl() != null && !rowData.getPropertyUrl().trim().isEmpty()) {
            String url = rowData.getPropertyUrl().toLowerCase();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                warnings.add("Property URL should start with http:// or https://");
            }
        }

        // Host ID and Host Name consistency check
        if (rowData.getHostId() != null && rowData.getHostName() != null) {
            // This could be enhanced to check against actual host master data
            if (rowData.getHostId().trim().isEmpty() && !rowData.getHostName().trim().isEmpty()) {
                warnings.add("Host ID is empty but Host Name is provided");
            }
        }
    }
}
