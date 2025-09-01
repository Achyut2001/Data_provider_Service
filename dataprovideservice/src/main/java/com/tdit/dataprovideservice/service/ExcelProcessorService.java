package com.tdit.dataprovideservice.service;

import com.tdit.dataprovideservice.dto.ExcelRowData;
import com.tdit.dataprovideservice.entity.Property;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ExcelProcessorService {

    private static final Logger log = LoggerFactory.getLogger(ExcelProcessorService.class);
    //Date should be in the format
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<ExcelRowData> processExcelFile(MultipartFile file) throws IOException {
        List<ExcelRowData> rows = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            int numberOfSheets = workbook.getNumberOfSheets();

            for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                if (sheet == null) continue;

                // Skip header row (row 0)
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        ExcelRowData rowData = extractRowData(row, i);
                        if (rowData != null) {
                            rows.add(rowData);
                        }
                    }
                }
            }
        }

        return rows;
    }

    /* converting entity into dto*/
    private ExcelRowData extractRowData(Row row, int rowNumber) {
        ExcelRowData rowData = new ExcelRowData();

        try {
            rowData.setPropertyId(getCellValue(row.getCell(0)));
            rowData.setPropertyTitle(getCellValue(row.getCell(1)));
            rowData.setDescription(getCellValue(row.getCell(2)));
            rowData.setPropertyType(getCellValue(row.getCell(3)));
            rowData.setAddressLine1(getCellValue(row.getCell(4)));
            rowData.setCity(getCellValue(row.getCell(5)));
            rowData.setState(getCellValue(row.getCell(6)));
            rowData.setCountry(getCellValue(row.getCell(7)));
            rowData.setPincode(getCellValue(row.getCell(8)));
            rowData.setLatitude(getCellValue(row.getCell(9)));
            rowData.setLongitude(getCellValue(row.getCell(10)));
            rowData.setHostId(getCellValue(row.getCell(11)));
            rowData.setHostName(getCellValue(row.getCell(12)));
            rowData.setHostContact(getCellValue(row.getCell(13)));
            rowData.setHostEmail(getCellValue(row.getCell(14)));
            rowData.setBasePrice(getCellValue(row.getCell(15)));
            rowData.setCurrency(getCellValue(row.getCell(16)));
            rowData.setAmenities(getCellValue(row.getCell(17))); //Single column for all amenities (CSV or JSON)
            rowData.setPropertyUrl(getCellValue(row.getCell(18)));
            rowData.setStatus(getCellValue(row.getCell(19)));
            rowData.setCreatedAt(getCellValue(row.getCell(20)));
            rowData.setUpdatedAt(getCellValue(row.getCell(21)));

            return rowData;
        } catch (Exception e) {
            log.error(" Error processing row {}: {}", rowNumber, e.getMessage(), e);
            return null;
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Format date-time correctly
                    return cell.getLocalDateTimeCellValue().format(DATE_FORMATTER);
                } else {
                    double numericValue = cell.getNumericCellValue();
                    // Check if it is integer
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case FORMULA:
                try {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                } catch (Exception e) {
                    return cell.getStringCellValue().trim();
                }

            case BLANK:
                return "";

            default:
                return "";
        }
    }


    public Property convertToProperty(ExcelRowData rowData) {
        Property property = new Property();

        // Property ID (only if provided)
        if (rowData.getPropertyId() != null && !rowData.getPropertyId().trim().isEmpty()) {
            try {
                property.setPropertyId(Long.parseLong(rowData.getPropertyId()));
            } catch (NumberFormatException e) {
                log.warn("Invalid Property ID: {}", rowData.getPropertyId());
            }
        }

        property.setPropertyTitle(rowData.getPropertyTitle());
        property.setDescription(rowData.getDescription());
        property.setPropertyType(rowData.getPropertyType());
        property.setAddressLine1(rowData.getAddressLine1());
        property.setCity(rowData.getCity());
        property.setState(rowData.getState());
        property.setCountry(rowData.getCountry());
        property.setPincode(rowData.getPincode());

        // Coordinates
        try {
            if (rowData.getLatitude() != null && !rowData.getLatitude().trim().isEmpty()) {
                property.setLatitude(Double.parseDouble(rowData.getLatitude()));
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid Latitude: {}", rowData.getLatitude());
        }

        try {
            if (rowData.getLongitude() != null && !rowData.getLongitude().trim().isEmpty()) {
                property.setLongitude(Double.parseDouble(rowData.getLongitude()));
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid Longitude: {}", rowData.getLongitude());
        }

        // Host details
        if (rowData.getHostId() != null) {
            try {
                property.setHostId(Long.parseLong(rowData.getHostId()));
            } catch (NumberFormatException e) {
                log.warn("Invalid Host ID: {}", rowData.getHostId());
            }
        }
        property.setHostName(rowData.getHostName());
        property.setHostContact(rowData.getHostContact());
        property.setHostEmail(rowData.getHostEmail());

        // Pricing
        if (rowData.getBasePrice() != null) {
            try {
                property.setBasePrice(Double.parseDouble(rowData.getBasePrice()));
            } catch (NumberFormatException e) {
                log.warn("Invalid Base Price: {}", rowData.getBasePrice());
            }
        }
        if (rowData.getCurrency() != null) {
            property.setCurrency(rowData.getCurrency().toUpperCase());
        }

        // ✅ Amenities: split CSV into List<String>
        if (rowData.getAmenities() != null && !rowData.getAmenities().trim().isEmpty()) {
            List<String> amenities = Arrays.asList(rowData.getAmenities().split(","));
            property.setAmenities(amenities);
        }

        property.setPropertyUrl(rowData.getPropertyUrl());
        if (rowData.getStatus() != null) {
            property.setStatus(rowData.getStatus().toUpperCase());
        }

        // Timestamps
        LocalDateTime now = LocalDateTime.now();
        try {
            if (rowData.getCreatedAt() != null && !rowData.getCreatedAt().trim().isEmpty()) {
                property.setCreatedAt(LocalDateTime.parse(rowData.getCreatedAt(), DATE_FORMATTER));
            } else {
                property.setCreatedAt(now);
            }
        } catch (Exception e) {
            property.setCreatedAt(now);
        }
        property.setUpdatedAt(now);

        return property;
    }
}
