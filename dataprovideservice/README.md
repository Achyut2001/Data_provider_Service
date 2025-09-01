# Data Provider Service

A Spring Boot service for uploading, validating, and managing property listings from Excel templates.

## Features

- **Excel Upload**: Process .xlsx files with property data
- **Comprehensive Validation**: Business rule validation for each row
- **Audit Tracking**: Complete audit trail of uploads and processing results
- **Bulk Processing**: Handle multiple properties in a single upload
- **Error Handling**: Detailed error reporting with row-level feedback
- **PostgreSQL Integration**: JSONB support for flexible data storage

## API Endpoints

### Upload Excel File
```
POST /api/excel/upload
Content-Type: multipart/form-data

Parameters:
- file: Excel file (.xlsx)
- uploadedBy: User identifier (optional, defaults to "system")
```

### Check Upload Status
```
GET /api/excel/status/{uploadId}
```

### Get Template Information
```
GET /api/excel/template
```

## Excel Template Structure

The service expects Excel files with the following columns (in order):

| Column | Field | Validation Rules |
|--------|-------|------------------|
| 1 | Property_ID | Optional for new records, mandatory for updates |
| 2 | Property_Title | Mandatory, max 150 characters |
| 3 | Description | Optional, max 500 characters |
| 4 | Property_Type | Mandatory: Apartment, Villa, PG, Hotel, Hostel |
| 5 | Address_Line1 | Mandatory |
| 6 | City | Mandatory |
| 7 | State | Mandatory |
| 8 | Country | Mandatory, ISO country name |
| 9 | Pincode | Mandatory, 5-10 digits |
| 10 | Latitude | Optional, -90 to 90 |
| 11 | Longitude | Optional, -180 to 180 |
| 12 | Host_ID | Mandatory, must exist in Host master |
| 13 | Host_Name | Mandatory, must match Host_ID |
| 14 | Host_Contact | Mandatory, 10-15 digit phone number |
| 15 | Host_Email | Mandatory, valid email format |
| 16 | Base_Price | Mandatory, numeric > 0 |
| 17 | Currency | Mandatory, ISO currency code (INR, USD, EUR, etc.) |
| 18 | Amenity_1 | Optional |
| 19 | Amenity_2 | Optional |
| 20 | Property/URL | Optional, valid URL if provided |
| 21 | Status | Mandatory: ACTIVE, INACTIVE, PENDING |
| 22 | createdAt | Auto-generated if blank, format: yyyy-MM-dd HH:mm:ss |
| 23 | updatedAt | Auto-generated during processing |

## Database Schema

### Properties Table
- `properties` - Main table for property data
- JSONB storage for amenities
- Proper indexing and constraints

### Upload Audit Table
- `upload_audit` - Tracks all uploads and processing results
- JSONB storage for row-level results
- Status tracking (PROCESSING, COMPLETED, FAILED)

## Setup Instructions

### Prerequisites
- Java 17
- Maven 3.6+
- PostgreSQL 12+

### Database Setup
1. Create PostgreSQL database:
```sql
CREATE DATABASE dataprovideservice;
```

2. Update `application.properties` with your database credentials

### Running the Application
```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8080`

## Validation Rules

The service uses **Spring's built-in validation framework** with comprehensive annotations:

### Mandatory Fields
- Property Title, Property Type, Address, City, State, Country
- Pincode, Host ID, Host Name, Host Contact, Host Email
- Base Price, Currency, Status

### Format Validation
- **Email**: `@Email` - Standard email format validation
- **Phone**: `@Pattern(regexp = "\\d{10,15}")` - 10-15 digits
- **Pincode**: `@Pattern(regexp = "\\d{5,10}")` - 5-10 digits
- **Coordinates**: `@DecimalMin`/`@DecimalMax` - Valid ranges
- **Price**: `@DecimalMin(value = "0.0", inclusive = false)` - Positive value

### Business Rules
- **Property Type**: `@Pattern(regexp = "^(Apartment|Villa|PG|Hotel|Hostel)$")`
- **Status**: `@Pattern(regexp = "^(ACTIVE|INACTIVE|PENDING)$")`
- **Currency**: `@ValidCurrency` - Custom validation for ISO currency codes
- **String Lengths**: `@Size(max = X)` - Enforced character limits

### Custom Validation
- **Currency**: Custom `@ValidCurrency` annotation supporting 20+ currencies
- **Date Format**: `@Pattern` for yyyy-MM-dd HH:mm:ss format
- **URL Format**: Regex validation for property URLs

## Error Handling

- **400 Bad Request**: Invalid file format
- **422 Unprocessable Entity**: Missing mandatory columns
- **500 Internal Server Error**: Database insertion failure

## Response Format

### Upload Response
```json
{
  "uploadId": "uuid",
  "fileName": "properties.xlsx",
  "totalRows": 100,
  "successRows": 95,
  "failedRows": 5,
  "warningRows": 2,
  "status": "COMPLETED",
  "message": "Processed 100 rows: 95 success, 5 failed, 2 warnings"
}
```

### Status Response
```json
{
  "uploadId": "uuid",
  "fileName": "properties.xlsx",
  "uploadedBy": "user123",
  "timestamp": "2024-01-01T10:00:00",
  "status": "COMPLETED",
  "rowResults": {
    "2": {
      "success": true,
      "rowNumber": 2
    },
    "3": {
      "success": false,
      "errorMessage": "Property title is mandatory",
      "rowNumber": 3
    }
  },
  "totalRows": 100,
  "successRows": 95,
  "failedRows": 5,
  "warningRows": 2
}
```

## Future Enhancements

- Role-based access control (Admin vs Host)
- Integration with downstream services
- CSV upload support
- Asynchronous processing with polling
- Real-time progress updates
- Email notifications for upload completion

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.
