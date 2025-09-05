package com.tdit.dataprovideservice.entity;
import java.util.List;

public final class Constants {

    private Constants() {} // prevent instantiation

    // Status values
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String UPLOAD_NOT_FOUND =  "Upload not found: ";


    // Error messages
    public static final String ERROR_ROW_EXTRACTION = "Failed to extract row data";
    public static final String ERROR_PROPERTY_CONVERSION = "Error converting to property: ";
    public static final String ERROR_PROCESSING_ROW = "Error processing row {}: {}";
    public static final String ERROR_PROCESSING_EXCEL_FILE = "Failed to process Excel file: ";


    // Audit messages
    public static final String MESSAGE_TEMPLATE = "Processed %d rows: %d success, %d failed, %d warnings";

    //Warn messages
    public static final String INVALID_STATUS_VALUE = "Invalid Status value '{}' found in Excel. Returning null.";
    public static final String INVALID_PROPERTY_TYPE_VALUE ="Invalid Property Type value '{}' found in Excel. Returning null.";
    public static final String INVALID_PROPERTY_ID = "Invalid Property ID: {}";
    public static final String INVALID_LATITUDE = "Invalid Latitude: {}";
    public static final String INVALID_LONGITUDE = "Invalid Longitude: {}";
    public static final String INVALID_HOST_ID = "Invalid Host ID: {}";
    public static final String INVALID_BASE_PRICE = "Invalid Base Price: {}";


    // =============================
    public static final List<String> PREFERRED_CURRENCIES =
            List.of("INR", "USD", "EUR", "GBP", "CAD", "AUD");

    // =============================
    // Latitude & Longitude Limits
    // =============================
    public static final double LAT_MIN = -90;
    public static final double LAT_MAX = 90;

    public static final double LNG_MIN = -180;
    public static final double LNG_MAX = 180;

    // =============================
    // Base Price
    // =============================
    public static final double BASE_PRICE_MIN = 0;

    // =============================
    // Error Messages
    // =============================
    public static final String ERROR_LAT_RANGE = "Latitude must be between -90 and 90";
    public static final String ERROR_LAT_NUMBER = "Latitude must be a valid number";

    public static final String ERROR_LNG_RANGE = "Longitude must be between -180 and 180";
    public static final String ERROR_LNG_NUMBER = "Longitude must be a valid number";

    public static final String ERROR_BASE_PRICE_RANGE = "Base price must be greater than 0";
    public static final String ERROR_BASE_PRICE_NUMBER = "Base price must be a valid number";

    // =============================
    // Warning Messages
    // =============================
    public static final String WARN_CURRENCY_NOT_PREFERRED = "Currency %s is not in preferred list";

    public static final String WARN_PROPERTY_URL = "Property URL should start with http:// or https://";

    public static final String WARN_HOST_ID_EMPTY = "Host ID is empty but Host Name is provided";
}
