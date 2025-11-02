package com.itech.itech_backend.modules.employee.controller;

import com.itech.itech_backend.modules.employee.entity.EmployeeProfile;
import com.itech.itech_backend.modules.employee.entity.EmployeeProfile.EmployeeStatus;
import com.itech.itech_backend.modules.employee.entity.EmployeeProfile.EmployeeType;
import com.itech.itech_backend.modules.employee.entity.EmployeeProfile.EmployeeRole;
import com.itech.itech_backend.modules.employee.service.EmployeeProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee/profiles")
@RequiredArgsConstructor
@Slf4j
public class EmployeeProfileController {

    private final EmployeeProfileService employeeProfileService;

    /**
     * Get all employee profiles with pagination and filtering
     */
    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Map<String, Object>> getAllEmployeeProfiles(
            Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(required = false) EmployeeType employeeType,
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) String workLocation) {
        try {
            log.info("👥 Get employee profiles request - page: {}, size: {}, filters: name={}, dept={}, designation={}", 
                    pageable.getPageNumber(), pageable.getPageSize(), name, department, designation);

            Page<EmployeeProfile> profiles = employeeProfileService.getEmployeeProfilesWithFilters(
                pageable, name, department, designation, status, employeeType, managerId, workLocation);

            Map<String, Object> response = createSuccessResponse("Employee profiles retrieved successfully", profiles);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting employee profiles: {}", e.getMessage(), e);
            return createErrorResponse("Failed to retrieve employee profiles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get employee profile by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Map<String, Object>> getEmployeeProfileById(@PathVariable Long id) {
        try {
            log.info("🔍 Get employee profile by ID request: {}", id);

            EmployeeProfile profile = employeeProfileService.getEmployeeProfileById(id);
            if (profile == null) {
                return createErrorResponse("Employee profile not found", HttpStatus.NOT_FOUND);
            }

            Map<String, Object> response = createSuccessResponse("Employee profile retrieved successfully", profile);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting employee profile by ID: {}", e.getMessage(), e);
            return createErrorResponse("Failed to retrieve employee profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get employee profile by employee code
     */
    @GetMapping("/by-code/{employeeCode}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Map<String, Object>> getEmployeeProfileByCode(@PathVariable String employeeCode) {
        try {
            log.info("🔍 Get employee profile by code request: {}", employeeCode);

            EmployeeProfile profile = employeeProfileService.getEmployeeProfileByCode(employeeCode);
            if (profile == null) {
                return createErrorResponse("Employee profile not found", HttpStatus.NOT_FOUND);
            }

            Map<String, Object> response = createSuccessResponse("Employee profile retrieved successfully", profile);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting employee profile by code: {}", e.getMessage(), e);
            return createErrorResponse("Failed to retrieve employee profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new employee profile
     */
    @PostMapping
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createEmployeeProfile(@Valid @RequestBody EmployeeProfile employeeProfile) {
        try {
            log.info("✅ Create employee profile request: {}", employeeProfile.getDisplayName());

            EmployeeProfile savedProfile = employeeProfileService.createEmployeeProfile(employeeProfile);

            Map<String, Object> response = createSuccessResponse("Employee profile created successfully", savedProfile);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid employee profile data: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("❌ Error creating employee profile: {}", e.getMessage(), e);
            return createErrorResponse("Failed to create employee profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing employee profile
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateEmployeeProfile(@PathVariable Long id, @Valid @RequestBody EmployeeProfile employeeProfile) {
        try {
            log.info("🔄 Update employee profile request: ID {}", id);

            EmployeeProfile updatedProfile = employeeProfileService.updateEmployeeProfile(id, employeeProfile);

            Map<String, Object> response = createSuccessResponse("Employee profile updated successfully", updatedProfile);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid employee profile data or profile not found: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("❌ Error updating employee profile: {}", e.getMessage(), e);
            return createErrorResponse("Failed to update employee profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update employee status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateEmployeeStatus(@PathVariable Long id, @RequestParam EmployeeStatus status) {
        try {
            log.info("🔄 Update employee status: ID {}, status: {}", id, status);

            EmployeeProfile updatedProfile = employeeProfileService.updateEmployeeStatus(id, status);

            Map<String, Object> response = createSuccessResponse("Employee status updated successfully", updatedProfile);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Employee profile not found: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("❌ Error updating employee status: {}", e.getMessage(), e);
            return createErrorResponse("Failed to update employee status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete an employee profile
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteEmployeeProfile(@PathVariable Long id) {
        try {
            log.info("🗑️ Delete employee profile request: ID {}", id);

            employeeProfileService.deleteEmployeeProfile(id);

            Map<String, Object> response = createSuccessResponse("Employee profile deleted successfully", null);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Employee profile not found: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("❌ Error deleting employee profile: {}", e.getMessage(), e);
            return createErrorResponse("Failed to delete employee profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Bulk update employee profiles
     */
    @PatchMapping("/bulk")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> bulkUpdateEmployeeProfiles(@RequestBody Map<String, Object> updateData) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> employeeIds = (List<Long>) updateData.get("employeeIds");
            
            log.info("🔄 Bulk update employee profiles request: {} profiles", employeeIds.size());

            List<EmployeeProfile> updatedProfiles = employeeProfileService.bulkUpdateEmployeeProfiles(employeeIds, updateData);

            Map<String, Object> response = createSuccessResponse(
                "Employee profiles updated successfully (" + updatedProfiles.size() + " profiles)", updatedProfiles);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error bulk updating employee profiles: {}", e.getMessage(), e);
            return createErrorResponse("Failed to update employee profiles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search employee profiles
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Map<String, Object>> searchEmployeeProfiles(
            @RequestParam String query,
            Pageable pageable) {
        try {
            log.info("🔍 Search employee profiles request: query '{}', page: {}, size: {}", 
                    query, pageable.getPageNumber(), pageable.getPageSize());

            Page<EmployeeProfile> profiles = employeeProfileService.searchEmployeeProfiles(query, pageable);

            Map<String, Object> response = createSuccessResponse("Employee profiles search completed", profiles);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error searching employee profiles: {}", e.getMessage(), e);
            return createErrorResponse("Failed to search employee profiles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get active employees
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveEmployees() {
        try {
            log.info("👥 Get active employees request");

            List<EmployeeProfile> employees = employeeProfileService.getActiveEmployees();

            Map<String, Object> response = createSuccessResponse("Active employees retrieved successfully", employees);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting active employees: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get active employees: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get employees for dropdown
     */
    @GetMapping("/dropdown")
    public ResponseEntity<Map<String, Object>> getEmployeesForDropdown(
            @RequestParam(required = false) String department) {
        try {
            log.info("📋 Get employees for dropdown: department: {}", department);

            List<Map<String, Object>> employees = employeeProfileService.getEmployeesForDropdown(department);

            Map<String, Object> response = createSuccessResponse("Employees for dropdown retrieved successfully", employees);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting employees for dropdown: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get employees for dropdown: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get employees by department
     */
    @GetMapping("/by-department/{department}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Map<String, Object>> getEmployeesByDepartment(@PathVariable String department) {
        try {
            log.info("🏢 Get employees by department: {}", department);

            List<EmployeeProfile> employees = employeeProfileService.getEmployeesByDepartment(department);

            Map<String, Object> response = createSuccessResponse("Employees retrieved successfully", employees);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting employees by department: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get employees: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get employees by manager
     */
    @GetMapping("/by-manager/{managerId}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Map<String, Object>> getEmployeesByManager(@PathVariable Long managerId) {
        try {
            log.info("👔 Get employees by manager: {}", managerId);

            List<EmployeeProfile> employees = employeeProfileService.getEmployeesByManager(managerId);

            Map<String, Object> response = createSuccessResponse("Team members retrieved successfully", employees);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting employees by manager: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get team members: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get managers
     */
    @GetMapping("/managers")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Map<String, Object>> getManagers() {
        try {
            log.info("👔 Get managers request");

            List<EmployeeProfile> managers = employeeProfileService.getManagers();

            Map<String, Object> response = createSuccessResponse("Managers retrieved successfully", managers);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting managers: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get managers: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get distinct departments
     */
    @GetMapping("/departments")
    public ResponseEntity<Map<String, Object>> getDistinctDepartments() {
        try {
            log.info("🏢 Get distinct departments request");

            List<String> departments = employeeProfileService.getDistinctDepartments();

            Map<String, Object> response = createSuccessResponse("Departments retrieved successfully", departments);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting departments: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get departments: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get distinct designations
     */
    @GetMapping("/designations")
    public ResponseEntity<Map<String, Object>> getDistinctDesignations() {
        try {
            log.info("💼 Get distinct designations request");

            List<String> designations = employeeProfileService.getDistinctDesignations();

            Map<String, Object> response = createSuccessResponse("Designations retrieved successfully", designations);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting designations: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get designations: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get distinct work locations
     */
    @GetMapping("/work-locations")
    public ResponseEntity<Map<String, Object>> getDistinctWorkLocations() {
        try {
            log.info("📍 Get distinct work locations request");

            List<String> workLocations = employeeProfileService.getDistinctWorkLocations();

            Map<String, Object> response = createSuccessResponse("Work locations retrieved successfully", workLocations);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting work locations: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get work locations: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get employee statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getEmployeeStatistics() {
        try {
            log.info("📊 Get employee statistics request");

            Map<String, Object> statistics = employeeProfileService.getEmployeeStatistics();

            Map<String, Object> response = createSuccessResponse("Employee statistics retrieved successfully", statistics);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting employee statistics: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get statistics: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get employees with birthdays today
     */
    @GetMapping("/birthdays-today")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Map<String, Object>> getEmployeesWithBirthdayToday() {
        try {
            log.info("🎂 Get employees with birthday today request");

            List<EmployeeProfile> employees = employeeProfileService.getEmployeesWithBirthdayToday();

            Map<String, Object> response = createSuccessResponse("Birthday employees retrieved successfully", employees);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting birthday employees: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get birthday employees: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get employees with work anniversary today
     */
    @GetMapping("/anniversaries-today")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Map<String, Object>> getEmployeesWithWorkAnniversaryToday() {
        try {
            log.info("🎉 Get employees with work anniversary today request");

            List<EmployeeProfile> employees = employeeProfileService.getEmployeesWithWorkAnniversaryToday();

            Map<String, Object> response = createSuccessResponse("Anniversary employees retrieved successfully", employees);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting anniversary employees: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get anniversary employees: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get employees with probation ending soon
     */
    @GetMapping("/probation-ending")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getEmployeesWithProbationEndingSoon(@RequestParam(defaultValue = "30") int days) {
        try {
            log.info("⏰ Get employees with probation ending in {} days", days);

            List<EmployeeProfile> employees = employeeProfileService.getEmployeesWithProbationEndingSoon(days);

            Map<String, Object> response = createSuccessResponse("Probation ending employees retrieved successfully", employees);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting probation ending employees: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get probation ending employees: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get recently joined employees
     */
    @GetMapping("/recently-joined")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getRecentlyJoinedEmployees(@RequestParam(defaultValue = "30") int days) {
        try {
            log.info("🆕 Get recently joined employees in last {} days", days);

            List<EmployeeProfile> employees = employeeProfileService.getRecentlyJoinedEmployees(days);

            Map<String, Object> response = createSuccessResponse("Recently joined employees retrieved successfully", employees);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting recently joined employees: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get recently joined employees: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Add role to employee
     */
    @PostMapping("/{id}/roles/{role}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> addRoleToEmployee(@PathVariable Long id, @PathVariable EmployeeRole role) {
        try {
            log.info("➕ Add role {} to employee ID: {}", role, id);

            EmployeeProfile updatedProfile = employeeProfileService.addRoleToEmployee(id, role);

            Map<String, Object> response = createSuccessResponse("Role added successfully", updatedProfile);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Employee profile not found: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("❌ Error adding role: {}", e.getMessage(), e);
            return createErrorResponse("Failed to add role: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Remove role from employee
     */
    @DeleteMapping("/{id}/roles/{role}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> removeRoleFromEmployee(@PathVariable Long id, @PathVariable EmployeeRole role) {
        try {
            log.info("➖ Remove role {} from employee ID: {}", role, id);

            EmployeeProfile updatedProfile = employeeProfileService.removeRoleFromEmployee(id, role);

            Map<String, Object> response = createSuccessResponse("Role removed successfully", updatedProfile);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Employee profile not found: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("❌ Error removing role: {}", e.getMessage(), e);
            return createErrorResponse("Failed to remove role: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Add permission to employee
     */
    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> addPermissionToEmployee(@PathVariable Long id, @RequestParam String permission) {
        try {
            log.info("➕ Add permission {} to employee ID: {}", permission, id);

            EmployeeProfile updatedProfile = employeeProfileService.addPermissionToEmployee(id, permission);

            Map<String, Object> response = createSuccessResponse("Permission added successfully", updatedProfile);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Employee profile not found: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("❌ Error adding permission: {}", e.getMessage(), e);
            return createErrorResponse("Failed to add permission: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Remove permission from employee
     */
    @DeleteMapping("/{id}/permissions")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> removePermissionFromEmployee(@PathVariable Long id, @RequestParam String permission) {
        try {
            log.info("➖ Remove permission {} from employee ID: {}", permission, id);

            EmployeeProfile updatedProfile = employeeProfileService.removePermissionFromEmployee(id, permission);

            Map<String, Object> response = createSuccessResponse("Permission removed successfully", updatedProfile);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Employee profile not found: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("❌ Error removing permission: {}", e.getMessage(), e);
            return createErrorResponse("Failed to remove permission: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Private helper methods

    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        return response;
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
