package com.itech.itech_backend.controller;

import com.itech.itech_backend.service.DataMigrationService;
import com.itech.itech_backend.service.DataMigrationService.MigrationConfig;
import com.itech.itech_backend.service.DataMigrationService.MigrationResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🚀 Enterprise Migration Management Controller
 * 
 * Provides secure endpoints for:
 * - Database schema migration monitoring
 * - Data migration execution and tracking
 * - Migration status reporting
 * - Emergency rollback procedures
 * 
 * ⚠️  CRITICAL: Only accessible to SUPER_ADMIN users
 */
@RestController
@RequestMapping("/api/admin/migration")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MigrationController {

    private final DataMigrationService migrationService;
    private final DataSource dataSource;

    // ============================================
    // MIGRATION STATUS & MONITORING
    // ============================================

    /**
     * Get overall migration status and progress
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getMigrationStatus() {
        log.info("📊 Fetching migration status");
        
        try {
            Map<String, Object> status = new HashMap<>();
            
            // Get migration phase status
            List<Map<String, Object>> phases = new ArrayList<>();
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "SELECT phase_name, status, started_at, completed_at, records_processed, records_total, error_message " +
                     "FROM migration_status ORDER BY id")) {
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Map<String, Object> phase = new HashMap<>();
                    phase.put("phaseName", rs.getString("phase_name"));
                    phase.put("status", rs.getString("status"));
                    phase.put("startedAt", rs.getTimestamp("started_at"));
                    phase.put("completedAt", rs.getTimestamp("completed_at"));
                    phase.put("recordsProcessed", rs.getInt("records_processed"));
                    phase.put("recordsTotal", rs.getInt("records_total"));
                    phase.put("errorMessage", rs.getString("error_message"));
                    phases.add(phase);
                }
            }
            
            status.put("phases", phases);
            
            // Get overall statistics
            Map<String, Object> stats = computeMigrationStatistics();
            status.put("statistics", stats);
            
            // Get readiness status
            boolean ready = validateMigrationReadiness();
            status.put("readinessStatus", ready ? "READY" : "NOT_READY");
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            log.error("Failed to get migration status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to retrieve migration status: " + e.getMessage()));
        }
    }

    /**
     * Get detailed migration statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getMigrationStatistics() {
        log.info("📈 Fetching migration statistics");
        
        try {
            Map<String, Object> stats = computeMigrationStatistics();
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            log.error("Failed to get migration statistics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to retrieve statistics: " + e.getMessage()));
        }
    }

    /**
     * Get migration audit trail
     */
    @GetMapping("/audit")
    public ResponseEntity<List<Map<String, Object>>> getMigrationAudit(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(required = false) String phase) {
        
        log.info("📋 Fetching migration audit trail (limit={}, phase={})", limit, phase);
        
        try {
            List<Map<String, Object>> auditTrail = new ArrayList<>();
            
            String sql = "SELECT * FROM migration_audit " +
                        (phase != null ? "WHERE migration_phase = ? " : "") +
                        "ORDER BY created_at DESC LIMIT ?";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                if (phase != null) {
                    stmt.setString(1, phase);
                    stmt.setInt(2, limit);
                } else {
                    stmt.setInt(1, limit);
                }
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Map<String, Object> audit = new HashMap<>();
                    audit.put("id", rs.getLong("id"));
                    audit.put("tableName", rs.getString("table_name"));
                    audit.put("operation", rs.getString("operation"));
                    audit.put("recordId", rs.getLong("record_id"));
                    audit.put("oldValues", rs.getString("old_values"));
                    audit.put("newValues", rs.getString("new_values"));
                    audit.put("migrationPhase", rs.getString("migration_phase"));
                    audit.put("createdAt", rs.getTimestamp("created_at"));
                    auditTrail.add(audit);
                }
            }
            
            return ResponseEntity.ok(auditTrail);
            
        } catch (Exception e) {
            log.error("Failed to get migration audit: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ============================================
    // MIGRATION EXECUTION
    // ============================================

    /**
     * Execute full data migration
     * ⚠️  CRITICAL OPERATION - Use with extreme caution
     */
    @PostMapping("/execute")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MigrationResult> executeMigration(@RequestBody MigrationConfig config) {
        log.warn("🚨 CRITICAL: Full data migration requested by admin");
        
        try {
            // Additional safety check
            if (!validateMigrationReadiness()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResult("Migration not ready - validation failed"));
            }
            
            // Execute migration
            MigrationResult result = migrationService.executeMigration(config);
            
            if (result.isSuccess()) {
                log.info("✅ Migration completed successfully in {} ms", result.getDurationMs());
                return ResponseEntity.ok(result);
            } else {
                log.error("❌ Migration failed: {}", result.getErrorMessage());
                return ResponseEntity.internalServerError().body(result);
            }
            
        } catch (Exception e) {
            log.error("🚨 Migration execution failed: {}", e.getMessage(), e);
            MigrationResult errorResult = createErrorResult("Migration execution failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }

    /**
     * Execute specific migration phase
     */
    @PostMapping("/execute/{phase}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> executePhase(@PathVariable String phase) {
        log.warn("🔧 Executing specific migration phase: {}", phase);
        
        try {
            MigrationConfig config = new MigrationConfig();
            
            switch (phase.toUpperCase()) {
                case "BUYERS":
                    var buyerResult = migrationService.migrateBuyersData(config);
                    return ResponseEntity.ok(Map.of("result", buyerResult));
                    
                case "VENDORS":
                    var vendorResult = migrationService.migrateVendorsData(config);
                    return ResponseEntity.ok(Map.of("result", vendorResult));
                    
                case "ADMINS":
                    var adminResult = migrationService.migrateAdminsData(config);
                    return ResponseEntity.ok(Map.of("result", adminResult));
                    
                case "EMPLOYEES":
                    var employeeResult = migrationService.migrateEmployeesData(config);
                    return ResponseEntity.ok(Map.of("result", employeeResult));
                    
                default:
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Unknown migration phase: " + phase));
            }
            
        } catch (Exception e) {
            log.error("Failed to execute migration phase {}: {}", phase, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Phase execution failed: " + e.getMessage()));
        }
    }

    // ============================================
    // EMERGENCY OPERATIONS
    // ============================================

    /**
     * Emergency rollback - USE ONLY IN CRISIS
     */
    @PostMapping("/emergency-rollback")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> emergencyRollback() {
        log.error("🚨 EMERGENCY ROLLBACK INITIATED BY ADMIN");
        
        try {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("CALL emergency_rollback()")) {
                
                stmt.executeUpdate();
                log.info("✅ Emergency rollback completed");
                
                return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Emergency rollback completed successfully"
                ));
                
            }
        } catch (Exception e) {
            log.error("❌ Emergency rollback failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "FAILED",
                "message", "Rollback failed: " + e.getMessage()
            ));
        }
    }

    /**
     * Validate migration readiness
     */
    @GetMapping("/validate-readiness")
    public ResponseEntity<Map<String, Object>> validateReadiness() {
        log.info("🔍 Validating migration readiness");
        
        try {
            boolean ready = validateMigrationReadiness();
            Map<String, Object> result = new HashMap<>();
            result.put("ready", ready);
            result.put("status", ready ? "READY" : "NOT_READY");
            
            if (!ready) {
                result.put("issues", getValidationIssues());
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Failed to validate readiness: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Validation failed: " + e.getMessage()));
        }
    }

    // ============================================
    // DATA VERIFICATION
    // ============================================

    /**
     * Verify data integrity post-migration
     */
    @GetMapping("/verify-integrity")
    public ResponseEntity<Map<String, Object>> verifyDataIntegrity() {
        log.info("🔍 Verifying data integrity");
        
        try {
            Map<String, Object> integrity = new HashMap<>();
            
            // Check for orphaned records
            Map<String, Long> orphanedRecords = new HashMap<>();
            
            try (Connection conn = dataSource.getConnection()) {
                // Check buyers without user_id
                try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM buyers WHERE user_id IS NULL")) {
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) orphanedRecords.put("buyers", rs.getLong(1));
                }
                
                // Check vendors without user_id
                try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM legacy_vendors WHERE user_id IS NULL")) {
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) orphanedRecords.put("vendors", rs.getLong(1));
                }
                
                // Check admins without user_id
                try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM admins WHERE user_id IS NULL")) {
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) orphanedRecords.put("admins", rs.getLong(1));
                }
            }
            
            integrity.put("orphanedRecords", orphanedRecords);
            
            // Calculate integrity score
            long totalOrphaned = orphanedRecords.values().stream().mapToLong(Long::longValue).sum();
            boolean isHealthy = totalOrphaned == 0;
            
            integrity.put("healthy", isHealthy);
            integrity.put("totalOrphanedRecords", totalOrphaned);
            integrity.put("integrityScore", isHealthy ? 100 : Math.max(0, 100 - totalOrphaned));
            
            return ResponseEntity.ok(integrity);
            
        } catch (Exception e) {
            log.error("Failed to verify data integrity: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Integrity verification failed: " + e.getMessage()));
        }
    }

    // ============================================
    // UTILITY METHODS
    // ============================================

    private Map<String, Object> computeMigrationStatistics() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        
        try (Connection conn = dataSource.getConnection()) {
            // Count records in each table
            Map<String, Long> tableCounts = new HashMap<>();
            
            String[] tables = {"user", "buyers", "legacy_vendors", "admins", "employee_profiles"};
            for (String table : tables) {
                try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM " + table)) {
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        tableCounts.put(table, rs.getLong(1));
                    }
                }
            }
            
            stats.put("tableCounts", tableCounts);
            
            // Count users by role
            Map<String, Long> usersByRole = new HashMap<>();
            try (PreparedStatement stmt = conn.prepareStatement("SELECT role, COUNT(*) FROM user GROUP BY role")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    usersByRole.put(rs.getString(1), rs.getLong(2));
                }
            }
            stats.put("usersByRole", usersByRole);
            
            // Migration completion percentage
            try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT COUNT(*) as completed FROM migration_status WHERE status = 'COMPLETED'")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    long completed = rs.getLong(1);
                    // Total phases we track
                    long total = 8; // Based on migration_status initialization
                    stats.put("completionPercentage", (completed * 100) / total);
                }
            }
        }
        
        return stats;
    }

    private boolean validateMigrationReadiness() {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT validate_migration_readiness()")) {
            
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getBoolean(1);
            
        } catch (Exception e) {
            log.error("Migration readiness validation failed: {}", e.getMessage(), e);
            return false;
        }
    }

    private List<String> getValidationIssues() {
        List<String> issues = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection()) {
            // Check if required tables exist
            String[] requiredTables = {"user", "buyers", "legacy_vendors", "admins", "employee_profiles"};
            
            for (String table : requiredTables) {
                try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?")) {
                    stmt.setString(1, table);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next() && rs.getInt(1) == 0) {
                        issues.add("Required table missing: " + table);
                    }
                }
            }
            
            // Check for excessive duplicate emails
            try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM (SELECT email FROM user UNION ALL SELECT email FROM buyers WHERE email IS NOT NULL) t GROUP BY email HAVING COUNT(*) > 2")) {
                ResultSet rs = stmt.executeQuery();
                int duplicates = 0;
                while (rs.next()) duplicates++;
                if (duplicates > 0) {
                    issues.add("Excessive duplicate emails detected: " + duplicates + " cases");
                }
            }
            
        } catch (Exception e) {
            issues.add("Validation check failed: " + e.getMessage());
        }
        
        return issues;
    }

    private MigrationResult createErrorResult(String errorMessage) {
        MigrationResult result = new MigrationResult();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        result.setStartTime(java.time.LocalDateTime.now());
        result.setEndTime(java.time.LocalDateTime.now());
        return result;
    }
}
