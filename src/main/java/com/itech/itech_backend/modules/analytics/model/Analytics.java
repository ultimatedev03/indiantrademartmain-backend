package com.itech.itech_backend.modules.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "analytics", indexes = {
    @Index(name = "idx_analytics_metric", columnList = "metric_name"),
    @Index(name = "idx_analytics_entity", columnList = "entity_type, entity_id"),
    @Index(name = "idx_analytics_date", columnList = "report_date"),
    @Index(name = "idx_analytics_period", columnList = "period_type, report_date"),
    @Index(name = "idx_analytics_category", columnList = "category")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Analytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===============================
    // METRIC IDENTIFICATION
    // ===============================
    
    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;
    
    @Column(name = "metric_code", length = 50)
    private String metricCode;
    
    @Column(name = "category", length = 50)
    private String category;
    
    @Column(name = "subcategory", length = 50)
    private String subcategory;

    // ===============================
    // ENTITY ASSOCIATION
    // ===============================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    private EntityType entityType;
    
    @Column(name = "entity_id")
    private Long entityId;
    
    @Column(name = "entity_name", length = 200)
    private String entityName;

    // ===============================
    // TIME DIMENSIONS
    // ===============================
    
    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false)
    private PeriodType periodType;
    
    @Column(name = "year")
    private Integer year;
    
    @Column(name = "month")
    private Integer month;
    
    @Column(name = "week")
    private Integer week;
    
    @Column(name = "day_of_year")
    private Integer dayOfYear;

    // ===============================
    // METRIC VALUES
    // ===============================
    
    @Column(name = "value_numeric", precision = 20, scale = 4)
    private BigDecimal valueNumeric;
    
    @Column(name = "value_count")
    private Long valueCount;
    
    @Column(name = "value_percentage", precision = 5, scale = 2)
    private BigDecimal valuePercentage;
    
    @Column(name = "value_text", length = 500)
    private String valueText;
    
    @Column(name = "value_boolean")
    private Boolean valueBoolean;

    // ===============================
    // AGGREGATED VALUES
    // ===============================
    
    @Column(name = "total_value", precision = 20, scale = 4)
    private BigDecimal totalValue;
    
    @Column(name = "average_value", precision = 20, scale = 4)
    private BigDecimal averageValue;
    
    @Column(name = "min_value", precision = 20, scale = 4)
    private BigDecimal minValue;
    
    @Column(name = "max_value", precision = 20, scale = 4)
    private BigDecimal maxValue;
    
    @Column(name = "median_value", precision = 20, scale = 4)
    private BigDecimal medianValue;

    // ===============================
    // COMPARISON VALUES
    // ===============================
    
    @Column(name = "previous_period_value", precision = 20, scale = 4)
    private BigDecimal previousPeriodValue;
    
    @Column(name = "growth_amount", precision = 20, scale = 4)
    private BigDecimal growthAmount;
    
    @Column(name = "growth_percentage", precision = 10, scale = 4)
    private BigDecimal growthPercentage;
    
    @Column(name = "target_value", precision = 20, scale = 4)
    private BigDecimal targetValue;
    
    @Column(name = "achievement_percentage", precision = 5, scale = 2)
    private BigDecimal achievementPercentage;

    // ===============================
    // METADATA
    // ===============================
    
    @Column(name = "unit_of_measure", length = 20)
    private String unitOfMeasure;
    
    @Column(name = "currency", length = 3)
    private String currency;
    
    @Column(name = "data_source", length = 100)
    private String dataSource;
    
    @Column(name = "calculation_method", length = 200)
    private String calculationMethod;

    // ===============================
    // DIMENSIONAL BREAKDOWNS
    // ===============================
    
    @Column(name = "dimension1_name", length = 50)
    private String dimension1Name;
    
    @Column(name = "dimension1_value", length = 100)
    private String dimension1Value;
    
    @Column(name = "dimension2_name", length = 50)
    private String dimension2Name;
    
    @Column(name = "dimension2_value", length = 100)
    private String dimension2Value;
    
    @Column(name = "dimension3_name", length = 50)
    private String dimension3Name;
    
    @Column(name = "dimension3_value", length = 100)
    private String dimension3Value;

    // ===============================
    // QUALITY AND CONFIDENCE
    // ===============================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "data_quality")
    private DataQuality dataQuality = DataQuality.HIGH;
    
    @Column(name = "confidence_level", precision = 3, scale = 2)
    private BigDecimal confidenceLevel;
    
    @Column(name = "sample_size")
    private Long sampleSize;
    
    @Column(name = "margin_of_error", precision = 5, scale = 4)
    private BigDecimal marginOfError;

    // ===============================
    // ADDITIONAL CONTEXT
    // ===============================
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Column(name = "notes", length = 2000)
    private String notes;
    
    @ElementCollection
    @CollectionTable(name = "analytics_tags", joinColumns = @JoinColumn(name = "analytics_id"))
    @Column(name = "tag")
    private java.util.List<String> tags;
    
    @ElementCollection
    @CollectionTable(name = "analytics_metadata", joinColumns = @JoinColumn(name = "analytics_id"))
    @MapKeyColumn(name = "metadata_key")
    @Column(name = "metadata_value")
    private Map<String, String> metadata;

    // ===============================
    // DERIVED INSIGHTS
    // ===============================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trend_direction")
    private TrendDirection trendDirection;
    
    @Column(name = "volatility_score", precision = 5, scale = 2)
    private BigDecimal volatilityScore;
    
    @Column(name = "seasonal_factor", precision = 5, scale = 4)
    private BigDecimal seasonalFactor;
    
    @Column(name = "anomaly_score", precision = 5, scale = 2)
    private BigDecimal anomalyScore;
    
    @Column(name = "is_anomaly")
    private Boolean isAnomaly = false;

    // ===============================
    // FORECASTING
    // ===============================
    
    @Column(name = "forecasted_value", precision = 20, scale = 4)
    private BigDecimal forecastedValue;
    
    @Column(name = "forecast_confidence", precision = 3, scale = 2)
    private BigDecimal forecastConfidence;
    
    @Column(name = "forecast_upper_bound", precision = 20, scale = 4)
    private BigDecimal forecastUpperBound;
    
    @Column(name = "forecast_lower_bound", precision = 20, scale = 4)
    private BigDecimal forecastLowerBound;

    // ===============================
    // AUDIT AND LINEAGE
    // ===============================
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "last_calculated_at")
    private LocalDateTime lastCalculatedAt;
    
    @Column(name = "calculation_duration_ms")
    private Long calculationDurationMs;
    
    @Column(name = "data_freshness_hours")
    private Integer dataFreshnessHours;

    // ===============================
    // ENUMS
    // ===============================
    
    public enum EntityType {
        COMPANY,
        BUYER,
        VENDOR,
        PRODUCT,
        ORDER,
        PAYMENT,
        CATEGORY,
        BRAND,
        REGION,
        SYSTEM,
        CAMPAIGN,
        GLOBAL
    }
    
    public enum PeriodType {
        DAILY,
        WEEKLY,
        MONTHLY,
        QUARTERLY,
        YEARLY,
        HOUR,
        REAL_TIME,
        CUSTOM
    }
    
    public enum DataQuality {
        LOW,
        MEDIUM,
        HIGH,
        PREMIUM
    }
    
    public enum TrendDirection {
        INCREASING,
        DECREASING,
        STABLE,
        VOLATILE,
        UNKNOWN
    }

    // ===============================
    // HELPER METHODS
    // ===============================
    
    public boolean isPositiveGrowth() {
        return growthPercentage != null && growthPercentage.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isTargetAchieved() {
        return achievementPercentage != null && 
               achievementPercentage.compareTo(BigDecimal.valueOf(100)) >= 0;
    }
    
    public boolean isHighQuality() {
        return dataQuality == DataQuality.HIGH || dataQuality == DataQuality.PREMIUM;
    }
    
    public boolean isRecent() {
        return dataFreshnessHours != null && dataFreshnessHours <= 24;
    }
    
    public String getFormattedValue() {
        if (valueNumeric != null) {
            if (currency != null) {
                return String.format("%.2f %s", valueNumeric, currency);
            } else if (unitOfMeasure != null) {
                return String.format("%.2f %s", valueNumeric, unitOfMeasure);
            }
            return valueNumeric.toString();
        } else if (valueCount != null) {
            return valueCount.toString();
        } else if (valuePercentage != null) {
            return String.format("%.2f%%", valuePercentage);
        } else if (valueText != null) {
            return valueText;
        } else if (valueBoolean != null) {
            return valueBoolean.toString();
        }
        return "N/A";
    }
    
    public void calculateGrowthMetrics() {
        if (valueNumeric != null && previousPeriodValue != null && 
            previousPeriodValue.compareTo(BigDecimal.ZERO) != 0) {
            
            growthAmount = valueNumeric.subtract(previousPeriodValue);
            growthPercentage = growthAmount
                .divide(previousPeriodValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        }
        
        if (valueNumeric != null && targetValue != null && 
            targetValue.compareTo(BigDecimal.ZERO) != 0) {
            
            achievementPercentage = valueNumeric
                .divide(targetValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        }
    }
    
    @PrePersist
    @PreUpdate
    protected void updateCalculations() {
        calculateGrowthMetrics();
        
        if (reportDate != null) {
            year = reportDate.getYear();
            month = reportDate.getMonthValue();
            dayOfYear = reportDate.getDayOfYear();
            // Calculate week of year
            week = (reportDate.getDayOfYear() - 1) / 7 + 1;
        }
    }
}

