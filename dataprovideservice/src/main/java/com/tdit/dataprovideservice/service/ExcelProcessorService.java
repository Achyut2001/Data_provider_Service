package com.tdit.dataprovideservice.service;

import com.tdit.dataprovideservice.dto.ExcelRowData;
      // Correct import for DTO enum
import com.tdit.dataprovideservice.entity.Constants;
import com.tdit.dataprovideservice.entity.Property;

import com.tdit.dataprovideservice.entity.Property_Type;
import com.tdit.dataprovideservice.entity.Status;
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
import java.util.List;

import static com.tdit.dataprovideservice.entity.Constants.*;

@Service
public class ExcelProcessorService {

    private static final Logger log = LoggerFactory.getLogger(ExcelProcessorService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<ExcelRowData> processExcelFile(MultipartFile file) throws IOException {
        List<ExcelRowData> rows = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            int numberOfSheets = workbook.getNumberOfSheets();

            for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                if (sheet == null) continue;

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

    private ExcelRowData extractRowData(Row row, int rowNumber) {
        ExcelRowData rowData = new ExcelRowData();

        try {
            rowData.setPropertyId(getCellValue(row.getCell(0)));
            rowData.setPropertyTitle(getCellValue(row.getCell(1)));
            rowData.setDescription(getCellValue(row.getCell(2)));
            rowData.setPropertyType(convertToPropertyType(getCellValue(row.getCell(3))));
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
            rowData.setAmenities(getCellValue(row.getCell(17)));
            rowData.setPropertyUrl(getCellValue(row.getCell(18)));
            rowData.setStatus(convertToStatus(getCellValue(row.getCell(19))));
            rowData.setCreatedAt(getCellValue(row.getCell(20)));
            rowData.setUpdatedAt(getCellValue(row.getCell(21)));

            return rowData;
        } catch (Exception e) {
            log.error(ERROR_PROCESSING_ROW, rowNumber, e.getMessage(), e);
            return null;
        }
    }

    private Status convertToStatus(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return null;
        }
        Status status = Status.fromString(statusStr);
        if (status == null) {
            log.warn(INVALID_STATUS_VALUE, statusStr);
        }
        return status;
    }


    private Property_Type convertToPropertyType(String typeStr) {
        if (typeStr == null || typeStr.trim().isEmpty()) {
            return null;
        }
        Property_Type type = Property_Type.fromString(typeStr);
        if (type == null) {
            log.warn(INVALID_PROPERTY_TYPE_VALUE, typeStr);
        }
        return type;
    }


    private String getCellValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().format(DATE_FORMATTER);
                } else {
                    double numericValue = cell.getNumericCellValue();
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

        if (rowData.getPropertyId() != null && !rowData.getPropertyId().trim().isEmpty()) {
            try {
                property.setPropertyId(Long.parseLong(rowData.getPropertyId()));
            } catch (NumberFormatException e) {
                log.warn(INVALID_PROPERTY_ID, rowData.getPropertyId());
            }
        }

        property.setPropertyTitle(rowData.getPropertyTitle());
        property.setDescription(rowData.getDescription());

        if (rowData.getPropertyType() != null) {
            property.setPropertyType(String.valueOf(rowData.getPropertyType()));
        }

        property.setAddressLine1(rowData.getAddressLine1());
        property.setCity(rowData.getCity());
        property.setState(rowData.getState());
        property.setCountry(rowData.getCountry());
        property.setPincode(rowData.getPincode());

        try {
            if (rowData.getLatitude() != null && !rowData.getLatitude().trim().isEmpty()) {
                property.setLatitude(Double.parseDouble(rowData.getLatitude()));
            }
        } catch (NumberFormatException e) {
            log.warn(INVALID_LATITUDE, rowData.getLatitude());
        }

        try {
            if (rowData.getLongitude() != null && !rowData.getLongitude().trim().isEmpty()) {
                property.setLongitude(Double.parseDouble(rowData.getLongitude()));
            }
        } catch (NumberFormatException e) {
            log.warn(INVALID_LONGITUDE, rowData.getLongitude());
        }

        if (rowData.getHostId() != null) {
            try {
                property.setHostId(Long.parseLong(rowData.getHostId()));
            } catch (NumberFormatException e) {
                log.warn(INVALID_HOST_ID, rowData.getHostId());
            }
        }
        property.setHostName(rowData.getHostName());
        property.setHostContact(rowData.getHostContact());
        property.setHostEmail(rowData.getHostEmail());

        if (rowData.getBasePrice() != null) {
            try {
                property.setBasePrice(Double.parseDouble(rowData.getBasePrice()));
            } catch (NumberFormatException e) {
                log.warn(INVALID_BASE_PRICE, rowData.getBasePrice());
            }
        }
        if (rowData.getCurrency() != null) {
            property.setCurrency(rowData.getCurrency().toUpperCase());
        }

        property.setAmenities(rowData.getAmenitiesList());
        property.setPropertyUrl(rowData.getPropertyUrl());

        if (rowData.getStatus() != null) {
            property.setStatus(rowData.getStatus().name());
        }

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