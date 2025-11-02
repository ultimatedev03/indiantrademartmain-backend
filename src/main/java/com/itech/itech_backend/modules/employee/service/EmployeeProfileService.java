package com.itech.itech_backend.modules.employee.service;

import com.itech.itech_backend.modules.employee.entity.EmployeeProfile;
import com.itech.itech_backend.modules.employee.entity.EmployeeProfile.EmployeeStatus;
import com.itech.itech_backend.modules.employee.entity.EmployeeProfile.EmployeeType;
import com.itech.itech_backend.modules.employee.entity.EmployeeProfile.EmployeeRole;
import com.itech.itech_backend.modules.employee.repository.EmployeeProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeeProfileService {

    private final EmployeeProfileRepository employeeProfileRepository;

    /**
     * Get employee profile by ID
     */
    @Transactional(readOnly = true)
    public EmployeeProfile getEmployeeProfileById(Long id) {
        log.debug("👤 Getting employee profile by ID: {}", id);
        return employeeProfileRepository.findById(id).orElse(null);
    }

    /**
     * Get employee profile by employee code
     */
    @Transactional(readOnly = true)
    public EmployeeProfile getEmployeeProfileByCode(String employeeCode) {
        log.debug("👤 Getting employee profile by code: {}", employeeCode);
        return employeeProfileRepository.findByEmployeeCode(employeeCode).orElse(null);
    }

    /**
     * Get employee profile by work email
     */
    @Transactional(readOnly = true)
    public EmployeeProfile getEmployeeProfileByEmail(String workEmail) {
        log.debug("👤 Getting employee profile by email: {}", workEmail);
        return employeeProfileRepository.findByWorkEmail(workEmail).orElse(null);
    }

    /**
     * Get employee profiles with filtering and pagination
     */
    @Transactional(readOnly = true)
    public Page<EmployeeProfile> getEmployeeProfilesWithFilters(Pageable pageable, String name, 
                                                               String department, String designation, 
                                                               EmployeeStatus status, EmployeeType employeeType,
                                                               Long managerId, String workLocation) {
        log.debug("🔍 Getting employee profiles with filters - name: {}, dept: {}, designation: {}", 
                 name, department, designation);
        
        return employeeProfileRepository.findWithFilters(name, department, designation, 
                                                        status, employeeType, managerId, workLocation, pageable);
    }

    /**
     * Create a new employee profile
     */
    public EmployeeProfile createEmployeeProfile(EmployeeProfile employeeProfile) {
        log.info("✅ Creating new employee profile: {}", employeeProfile.getDisplayName());
        
        // Validate employee data
        validateEmployeeProfileData(employeeProfile, null);
        
        // Check for duplicates
        if (employeeProfileRepository.existsByEmployeeCodeIgnoreCase(employeeProfile.getEmployeeCode())) {
            throw new IllegalArgumentException("Employee code '" + employeeProfile.getEmployeeCode() + "' already exists");
        }
        
        if (employeeProfileRepository.existsByWorkEmailIgnoreCase(employeeProfile.getWorkEmail())) {
            throw new IllegalArgumentException("Work email '" + employeeProfile.getWorkEmail() + "' already exists");
        }
        
        // Set defaults
        if (employeeProfile.getStatus() == null) {
            employeeProfile.setStatus(EmployeeStatus.ACTIVE);
        }
        if (employeeProfile.getEmployeeType() == null) {
            employeeProfile.setEmployeeType(EmployeeType.FULL_TIME);
        }
        if (employeeProfile.getRoles() == null || employeeProfile.getRoles().isEmpty()) {
            employeeProfile.setRoles(Set.of(EmployeeRole.EMPLOYEE));
        }
        
        return employeeProfileRepository.save(employeeProfile);
    }

    /**
     * Update an existing employee profile
     */
    public EmployeeProfile updateEmployeeProfile(Long id, EmployeeProfile updatedProfile) {
        log.info("🔄 Updating employee profile ID: {}", id);
        
        EmployeeProfile existingProfile = employeeProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee profile not found with ID: " + id));
        
        // Validate updated data
        validateEmployeeProfileData(updatedProfile, id);
        
        // Check for duplicates (excluding current profile)
        if (!existingProfile.getEmployeeCode().equalsIgnoreCase(updatedProfile.getEmployeeCode())) {
            if (employeeProfileRepository.existsByEmployeeCodeAndIdNot(updatedProfile.getEmployeeCode(), id)) {
                throw new IllegalArgumentException("Employee code '" + updatedProfile.getEmployeeCode() + "' already exists");
            }
        }
        
        if (!existingProfile.getWorkEmail().equalsIgnoreCase(updatedProfile.getWorkEmail())) {
            if (employeeProfileRepository.existsByWorkEmailAndIdNot(updatedProfile.getWorkEmail(), id)) {
                throw new IllegalArgumentException("Work email '" + updatedProfile.getWorkEmail() + "' already exists");
            }
        }
        
        // Update fields
        updateEmployeeFields(existingProfile, updatedProfile);
        
        return employeeProfileRepository.save(existingProfile);
    }

    /**
     * Update employee status
     */
    public EmployeeProfile updateEmployeeStatus(Long id, EmployeeStatus status) {
        log.info("🔄 Updating employee status for ID: {} to {}", id, status);
        
        EmployeeProfile employee = employeeProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee profile not found with ID: " + id));
        
        employee.setStatus(status);
        
        // Set relevant dates based on status
        switch (status) {
            case CONFIRMED -> employee.setConfirmationDate(LocalDate.now());
            case RESIGNED -> {
                if (employee.getResignationDate() == null) {
                    employee.setResignationDate(LocalDate.now());
                }
            }
            case TERMINATED -> employee.setLastWorkingDay(LocalDate.now());
        }
        
        return employeeProfileRepository.save(employee);
    }

    /**
     * Delete employee profile (soft delete by marking inactive)
     */
    public void deleteEmployeeProfile(Long id) {
        log.info("🗑️ Deleting employee profile ID: {}", id);
        
        EmployeeProfile employee = employeeProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee profile not found with ID: " + id));
        
        // Soft delete by marking as terminated
        employee.setStatus(EmployeeStatus.TERMINATED);
        employee.setLastWorkingDay(LocalDate.now());
        employeeProfileRepository.save(employee);
    }

    /**
     * Bulk update employee profiles
     */
    public List<EmployeeProfile> bulkUpdateEmployeeProfiles(List<Long> employeeIds, Map<String, Object> updateData) {
        log.info("🔄 Bulk updating {} employee profiles", employeeIds.size());
        
        List<EmployeeProfile> employees = employeeProfileRepository.findAllById(employeeIds);
        
        for (EmployeeProfile employee : employees) {
            // Apply bulk updates
            if (updateData.containsKey("status")) {
                employee.setStatus(EmployeeStatus.valueOf(updateData.get("status").toString()));
            }
            if (updateData.containsKey("department")) {
                employee.setDepartment(updateData.get("department").toString());
            }
            if (updateData.containsKey("managerId")) {
                employee.setManagerId(updateData.get("managerId") != null ? 
                    Long.valueOf(updateData.get("managerId").toString()) : null);
            }
            if (updateData.containsKey("workLocation")) {
                employee.setWorkLocation(updateData.get("workLocation").toString());
            }
        }
        
        return employeeProfileRepository.saveAll(employees);
    }

    /**
     * Search employee profiles
     */
    @Transactional(readOnly = true)
    public Page<EmployeeProfile> searchEmployeeProfiles(String query, Pageable pageable) {
        log.debug("🔍 Searching employee profiles with query: {}", query);
        return employeeProfileRepository.searchByTerm(query, pageable);
    }

    /**
     * Get active employees
     */
    @Transactional(readOnly = true)
    public List<EmployeeProfile> getActiveEmployees() {
        log.debug("👥 Getting active employees");
        return employeeProfileRepository.findActiveEmployees();
    }

    /**
     * Get employees for dropdown
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getEmployeesForDropdown(String department) {
        log.debug("📋 Getting employees for dropdown - department: {}", department);
        
        List<Object[]> results = department != null ? 
            employeeProfileRepository.findEmployeesForDropdownByDepartment(department) :
            employeeProfileRepository.findEmployeesForDropdown();
        
        return results.stream()
                .map(result -> {
                    Map<String, Object> employee = new HashMap<>();
                    employee.put("id", result[0]);
                    employee.put("name", result[1]);
                    employee.put("employeeCode", result[2]);
                    return employee;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get employees by department
     */
    @Transactional(readOnly = true)
    public List<EmployeeProfile> getEmployeesByDepartment(String department) {
        log.debug("👥 Getting employees by department: {}", department);
        return employeeProfileRepository.findActiveEmployeesByDepartment(department);
    }

    /**
     * Get employees by manager
     */
    @Transactional(readOnly = true)
    public List<EmployeeProfile> getEmployeesByManager(Long managerId) {
        log.debug("👥 Getting employees by manager: {}", managerId);
        return employeeProfileRepository.findActiveEmployeesByManager(managerId);
    }

    /**
     * Get managers
     */
    @Transactional(readOnly = true)
    public List<EmployeeProfile> getManagers() {
        log.debug("👔 Getting managers");
        return employeeProfileRepository.findManagers();
    }

    /**
     * Get distinct departments
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctDepartments() {
        log.debug("🏢 Getting distinct departments");
        return employeeProfileRepository.findDistinctDepartments();
    }

    /**
     * Get distinct designations
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctDesignations() {
        log.debug("💼 Getting distinct designations");
        return employeeProfileRepository.findDistinctDesignations();
    }

    /**
     * Get distinct work locations
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctWorkLocations() {
        log.debug("📍 Getting distinct work locations");
        return employeeProfileRepository.findDistinctWorkLocations();
    }

    /**
     * Get employee statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getEmployeeStatistics() {
        log.debug("📊 Getting employee statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalEmployees", employeeProfileRepository.count());
        stats.put("activeEmployees", employeeProfileRepository.countActiveEmployees());
        stats.put("incompleteProfiles", employeeProfileRepository.countIncompleteProfiles());
        stats.put("unverifiedDocuments", employeeProfileRepository.countUnverifiedDocuments());
        
        // Department breakdown
        List<Object[]> deptStats = employeeProfileRepository.getEmployeeCountByDepartment();
        Map<String, Long> departmentBreakdown = new HashMap<>();
        for (Object[] stat : deptStats) {
            departmentBreakdown.put((String) stat[0], (Long) stat[1]);
        }
        stats.put("employeesByDepartment", departmentBreakdown);
        
        // Status breakdown
        List<Object[]> statusStats = employeeProfileRepository.getEmployeeCountByStatus();
        Map<String, Long> statusBreakdown = new HashMap<>();
        for (Object[] stat : statusStats) {
            statusBreakdown.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("employeesByStatus", statusBreakdown);
        
        // Employee type breakdown
        List<Object[]> typeStats = employeeProfileRepository.getEmployeeCountByType();
        Map<String, Long> typeBreakdown = new HashMap<>();
        for (Object[] stat : typeStats) {
            typeBreakdown.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("employeesByType", typeBreakdown);
        
        return stats;
    }

    /**
     * Get employees with birthdays today
     */
    @Transactional(readOnly = true)
    public List<EmployeeProfile> getEmployeesWithBirthdayToday() {
        log.debug("🎂 Getting employees with birthday today");
        return employeeProfileRepository.findEmployeesWithBirthdayOn(LocalDate.now());
    }

    /**
     * Get employees with work anniversary today
     */
    @Transactional(readOnly = true)
    public List<EmployeeProfile> getEmployeesWithWorkAnniversaryToday() {
        log.debug("🎉 Getting employees with work anniversary today");
        return employeeProfileRepository.findEmployeesWithWorkAnniversaryOn(LocalDate.now());
    }

    /**
     * Get employees with probation ending soon
     */
    @Transactional(readOnly = true)
    public List<EmployeeProfile> getEmployeesWithProbationEndingSoon(int days) {
        log.debug("⏰ Getting employees with probation ending in {} days", days);
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);
        return employeeProfileRepository.findEmployeesWithProbationEndingSoon(startDate, endDate);
    }

    /**
     * Get recently joined employees
     */
    @Transactional(readOnly = true)
    public List<EmployeeProfile> getRecentlyJoinedEmployees(int days) {
        log.debug("🆕 Getting employees joined in last {} days", days);
        LocalDate startDate = LocalDate.now().minusDays(days);
        LocalDate endDate = LocalDate.now();
        return employeeProfileRepository.findEmployeesJoinedBetween(startDate, endDate);
    }

    /**
     * Add role to employee
     */
    public EmployeeProfile addRoleToEmployee(Long employeeId, EmployeeRole role) {
        log.info("➕ Adding role {} to employee ID: {}", role, employeeId);
        
        EmployeeProfile employee = employeeProfileRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee profile not found with ID: " + employeeId));
        
        employee.getRoles().add(role);
        return employeeProfileRepository.save(employee);
    }

    /**
     * Remove role from employee
     */
    public EmployeeProfile removeRoleFromEmployee(Long employeeId, EmployeeRole role) {
        log.info("➖ Removing role {} from employee ID: {}", role, employeeId);
        
        EmployeeProfile employee = employeeProfileRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee profile not found with ID: " + employeeId));
        
        employee.getRoles().remove(role);
        return employeeProfileRepository.save(employee);
    }

    /**
     * Add permission to employee
     */
    public EmployeeProfile addPermissionToEmployee(Long employeeId, String permission) {
        log.info("➕ Adding permission {} to employee ID: {}", permission, employeeId);
        
        EmployeeProfile employee = employeeProfileRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee profile not found with ID: " + employeeId));
        
        employee.getPermissions().add(permission);
        return employeeProfileRepository.save(employee);
    }

    /**
     * Remove permission from employee
     */
    public EmployeeProfile removePermissionFromEmployee(Long employeeId, String permission) {
        log.info("➖ Removing permission {} from employee ID: {}", permission, employeeId);
        
        EmployeeProfile employee = employeeProfileRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee profile not found with ID: " + employeeId));
        
        employee.getPermissions().remove(permission);
        return employeeProfileRepository.save(employee);
    }

    // Private helper methods

    private void validateEmployeeProfileData(EmployeeProfile profile, Long excludeId) {
        if (profile.getEmployeeCode() == null || profile.getEmployeeCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee code is required");
        }
        
        if (profile.getFirstName() == null || profile.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        
        if (profile.getLastName() == null || profile.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        
        if (profile.getWorkEmail() == null || profile.getWorkEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Work email is required");
        }
        
        if (profile.getDepartment() == null || profile.getDepartment().trim().isEmpty()) {
            throw new IllegalArgumentException("Department is required");
        }
        
        if (profile.getDesignation() == null || profile.getDesignation().trim().isEmpty()) {
            throw new IllegalArgumentException("Designation is required");
        }
        
        if (profile.getJoiningDate() == null) {
            throw new IllegalArgumentException("Joining date is required");
        }
        
        if (profile.getJoiningDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Joining date cannot be in the future");
        }
    }

    private void updateEmployeeFields(EmployeeProfile existing, EmployeeProfile updated) {
        existing.setEmployeeCode(updated.getEmployeeCode());
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setMiddleName(updated.getMiddleName());
        existing.setDateOfBirth(updated.getDateOfBirth());
        existing.setGender(updated.getGender());
        existing.setWorkEmail(updated.getWorkEmail());
        existing.setWorkPhone(updated.getWorkPhone());
        existing.setPersonalMobile(updated.getPersonalMobile());
        existing.setPersonalEmail(updated.getPersonalEmail());
        existing.setCurrentAddress(updated.getCurrentAddress());
        existing.setPermanentAddress(updated.getPermanentAddress());
        existing.setCity(updated.getCity());
        existing.setState(updated.getState());
        existing.setCountry(updated.getCountry());
        existing.setPostalCode(updated.getPostalCode());
        existing.setDepartment(updated.getDepartment());
        existing.setDesignation(updated.getDesignation());
        existing.setEmployeeType(updated.getEmployeeType());
        existing.setStatus(updated.getStatus());
        existing.setJoiningDate(updated.getJoiningDate());
        existing.setProbationEndDate(updated.getProbationEndDate());
        existing.setConfirmationDate(updated.getConfirmationDate());
        existing.setResignationDate(updated.getResignationDate());
        existing.setLastWorkingDay(updated.getLastWorkingDay());
        existing.setManagerId(updated.getManagerId());
        existing.setWorkLocation(updated.getWorkLocation());
        existing.setFloorCabin(updated.getFloorCabin());
        existing.setEmergencyContactName(updated.getEmergencyContactName());
        existing.setEmergencyContactRelation(updated.getEmergencyContactRelation());
        existing.setEmergencyContactPhone(updated.getEmergencyContactPhone());
        existing.setSkills(updated.getSkills());
        existing.setQualification(updated.getQualification());
        existing.setYearsOfExperience(updated.getYearsOfExperience());
        existing.setPanNumber(updated.getPanNumber());
        existing.setAadhaarNumber(updated.getAadhaarNumber());
        existing.setPassportNumber(updated.getPassportNumber());
        existing.setBankName(updated.getBankName());
        existing.setAccountNumber(updated.getAccountNumber());
        existing.setIfscCode(updated.getIfscCode());
        existing.setBio(updated.getBio());
        existing.setProfilePictureUrl(updated.getProfilePictureUrl());
        existing.setNotes(updated.getNotes());
        existing.setIsDocumentsVerified(updated.getIsDocumentsVerified());
        
        if (updated.getRoles() != null) {
            existing.setRoles(updated.getRoles());
        }
        if (updated.getPermissions() != null) {
            existing.setPermissions(updated.getPermissions());
        }
    }
}
