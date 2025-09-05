package com.tdit.dataprovideservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "upload_audit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uploadId;

    private String fileName;
    private String uploadedBy;
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private UploadStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<Integer, RowResult> rowResults;

    private Integer totalRows;
    private Integer successRows;
    private Integer failedRows;
    private Integer warningRows;

    public enum UploadStatus {
        PROCESSING, COMPLETED, FAILED
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RowResult {
        private boolean success;
        private String errorMessage;
        private String warningMessage;
        private Integer rowNumber;
    }
}
