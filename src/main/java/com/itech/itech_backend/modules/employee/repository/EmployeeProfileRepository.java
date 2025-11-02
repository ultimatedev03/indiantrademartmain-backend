package com.itech.itech_backend.modules.employee.repository;

import com.itech.itech_backend.modules.employee.entity.EmployeeProfile;
import com.itech.itech_backend.modules.employee.entity.EmployeeProfile.EmployeeStatus;
import com.itech.itech_backend.modules.employee.entity.EmployeeProfile.EmployeeType;
import com.itech.itech_backend.modules.employee.entity.EmployeeProfile.EmployeeRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {

    // Basic finder methods
    Optional<EmployeeProfile> findByEmployeeCode(String employeeCode);
    
    // Migration support methods  
    List<EmployeeProfile> findByUserIsNull();
    Optional<EmployeeProfile> findByWorkEmail(String workEmail);
    Optional<EmployeeProfile> findByUserId(Long userId);
    List<EmployeeProfile> findByDepartment(String department);
    List<EmployeeProfile> findByDesignation(String designation);
    List<EmployeeProfile> findByStatus(EmployeeStatus status);
    List<EmployeeProfile> findByEmployeeType(EmployeeType employeeType);
    List<EmployeeProfile> findByManagerId(Long managerId);

    // Search methods
    @Query("SELECT e FROM EmployeeProfile e WHERE " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.workEmail) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.department) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.designation) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<EmployeeProfile> searchByTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT e FROM EmployeeProfile e WHERE " +
           "(:name IS NULL OR LOWER(CONCAT(e.firstName, ' ', e.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:department IS NULL OR LOWER(e.department) = LOWER(:department)) AND " +
           "(:designation IS NULL OR LOWER(e.designation) = LOWER(:designation)) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:employeeType IS NULL OR e.employeeType = :employeeType) AND " +
           "(:managerId IS NULL OR e.managerId = :managerId) AND " +
           "(:workLocation IS NULL OR LOWER(e.workLocation) = LOWER(:workLocation))")
    Page<EmployeeProfile> findWithFilters(@Param("name") String name,
                                        @Param("department") String department,
                                        @Param("designation") String designation,
                                        @Param("status") EmployeeStatus status,
                                        @Param("employeeType") EmployeeType employeeType,
                                        @Param("managerId") Long managerId,
                                        @Param("workLocation") String workLocation,
                                        Pageable pageable);

    // Active employees
    @Query("SELECT e FROM EmployeeProfile e WHERE e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION') " +
           "ORDER BY e.firstName ASC, e.lastName ASC")
    List<EmployeeProfile> findActiveEmployees();

    @Query("SELECT e FROM EmployeeProfile e WHERE e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION') " +
           "ORDER BY e.firstName ASC, e.lastName ASC")
    Page<EmployeeProfile> findActiveEmployees(Pageable pageable);

    // Employees by department
    @Query("SELECT e FROM EmployeeProfile e WHERE LOWER(e.department) = LOWER(:department) " +
           "AND e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION') " +
           "ORDER BY e.designation, e.firstName")
    List<EmployeeProfile> findActiveEmployeesByDepartment(@Param("department") String department);

    // Employees by manager
    @Query("SELECT e FROM EmployeeProfile e WHERE e.managerId = :managerId " +
           "AND e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION') " +
           "ORDER BY e.firstName ASC, e.lastName ASC")
    List<EmployeeProfile> findActiveEmployeesByManager(@Param("managerId") Long managerId);

    // Managers (employees who have reportees)
    @Query("SELECT DISTINCT e FROM EmployeeProfile e WHERE e.id IN " +
           "(SELECT emp.managerId FROM EmployeeProfile emp WHERE emp.managerId IS NOT NULL) " +
           "AND e.status IN ('ACTIVE', 'CONFIRMED') " +
           "ORDER BY e.firstName ASC, e.lastName ASC")
    List<EmployeeProfile> findManagers();

    // Employees joining in date range
    @Query("SELECT e FROM EmployeeProfile e WHERE e.joiningDate BETWEEN :startDate AND :endDate " +
           "ORDER BY e.joiningDate DESC")
    List<EmployeeProfile> findEmployeesJoinedBetween(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    // Employees with probation ending soon
    @Query("SELECT e FROM EmployeeProfile e WHERE e.status = 'ON_PROBATION' " +
           "AND e.probationEndDate BETWEEN :startDate AND :endDate " +
           "ORDER BY e.probationEndDate ASC")
    List<EmployeeProfile> findEmployeesWithProbationEndingSoon(@Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate);

    // Birthday employees
    @Query("SELECT e FROM EmployeeProfile e WHERE " +
           "MONTH(e.dateOfBirth) = MONTH(:date) AND DAY(e.dateOfBirth) = DAY(:date) " +
           "AND e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION') " +
           "ORDER BY e.firstName ASC")
    List<EmployeeProfile> findEmployeesWithBirthdayOn(@Param("date") LocalDate date);

    // Work anniversary
    @Query("SELECT e FROM EmployeeProfile e WHERE " +
           "MONTH(e.joiningDate) = MONTH(:date) AND DAY(e.joiningDate) = DAY(:date) " +
           "AND e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION') " +
           "ORDER BY e.firstName ASC")
    List<EmployeeProfile> findEmployeesWithWorkAnniversaryOn(@Param("date") LocalDate date);

    // Distinct values for filtering
    @Query("SELECT DISTINCT e.department FROM EmployeeProfile e WHERE e.department IS NOT NULL ORDER BY e.department")
    List<String> findDistinctDepartments();

    @Query("SELECT DISTINCT e.designation FROM EmployeeProfile e WHERE e.designation IS NOT NULL ORDER BY e.designation")
    List<String> findDistinctDesignations();

    @Query("SELECT DISTINCT e.workLocation FROM EmployeeProfile e WHERE e.workLocation IS NOT NULL ORDER BY e.workLocation")
    List<String> findDistinctWorkLocations();

    // Existence checks
    boolean existsByEmployeeCode(String employeeCode);
    boolean existsByWorkEmail(String workEmail);
    boolean existsByEmployeeCodeAndIdNot(String employeeCode, Long id);
    boolean existsByWorkEmailAndIdNot(String workEmail, Long id);
    boolean existsByUserId(Long userId);

    // Counting methods
    long countByDepartment(String department);
    long countByStatus(EmployeeStatus status);
    long countByEmployeeType(EmployeeType employeeType);
    long countByManagerId(Long managerId);

    @Query("SELECT COUNT(e) FROM EmployeeProfile e WHERE e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION')")
    long countActiveEmployees();

    @Query("SELECT COUNT(e) FROM EmployeeProfile e WHERE e.isProfileComplete = false " +
           "AND e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION')")
    long countIncompleteProfiles();

    @Query("SELECT COUNT(e) FROM EmployeeProfile e WHERE e.isDocumentsVerified = false " +
           "AND e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION')")
    long countUnverifiedDocuments();

    // Statistics queries
    @Query("SELECT e.department, COUNT(e) FROM EmployeeProfile e " +
           "WHERE e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION') " +
           "GROUP BY e.department ORDER BY COUNT(e) DESC")
    List<Object[]> getEmployeeCountByDepartment();

    @Query("SELECT e.designation, COUNT(e) FROM EmployeeProfile e " +
           "WHERE e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION') " +
           "GROUP BY e.designation ORDER BY COUNT(e) DESC")
    List<Object[]> getEmployeeCountByDesignation();

    @Query("SELECT e.employeeType, COUNT(e) FROM EmployeeProfile e " +
           "WHERE e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION') " +
           "GROUP BY e.employeeType ORDER BY COUNT(e) DESC")
    List<Object[]> getEmployeeCountByType();

    @Query("SELECT e.status, COUNT(e) FROM EmployeeProfile e " +
           "GROUP BY e.status ORDER BY COUNT(e) DESC")
    List<Object[]> getEmployeeCountByStatus();

    // Employees with roles
    @Query("SELECT e FROM EmployeeProfile e JOIN e.roles r WHERE r = :role " +
           "AND e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION') " +
           "ORDER BY e.firstName ASC, e.lastName ASC")
    List<EmployeeProfile> findEmployeesWithRole(@Param("role") EmployeeRole role);

    // Employees with permissions
    @Query("SELECT e FROM EmployeeProfile e JOIN e.permissions p WHERE p = :permission " +
           "AND e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION') " +
           "ORDER BY e.firstName ASC, e.lastName ASC")
    List<EmployeeProfile> findEmployeesWithPermission(@Param("permission") String permission);

    // Bulk operations
    @Modifying
    @Query("UPDATE EmployeeProfile e SET e.status = :status WHERE e.id IN :ids")
    int bulkUpdateStatus(@Param("ids") List<Long> ids, @Param("status") EmployeeStatus status);

    @Modifying
    @Query("UPDATE EmployeeProfile e SET e.department = :department WHERE e.id IN :ids")
    int bulkUpdateDepartment(@Param("ids") List<Long> ids, @Param("department") String department);

    @Modifying
    @Query("UPDATE EmployeeProfile e SET e.managerId = :managerId WHERE e.id IN :ids")
    int bulkUpdateManager(@Param("ids") List<Long> ids, @Param("managerId") Long managerId);

    // For dropdown/select purposes
    @Query("SELECT e.id, CONCAT(e.firstName, ' ', e.lastName), e.employeeCode FROM EmployeeProfile e " +
           "WHERE e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION') " +
           "ORDER BY e.firstName ASC, e.lastName ASC")
    List<Object[]> findEmployeesForDropdown();

    @Query("SELECT e.id, CONCAT(e.firstName, ' ', e.lastName), e.employeeCode FROM EmployeeProfile e " +
           "WHERE e.department = :department AND e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION') " +
           "ORDER BY e.firstName ASC, e.lastName ASC")
    List<Object[]> findEmployeesForDropdownByDepartment(@Param("department") String department);

    // Recently joined employees
    @Query("SELECT e FROM EmployeeProfile e WHERE e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION') " +
           "ORDER BY e.joiningDate DESC")
    Page<EmployeeProfile> findRecentlyJoinedEmployees(Pageable pageable);

    // Senior employees (by joining date)
    @Query("SELECT e FROM EmployeeProfile e WHERE e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION') " +
           "ORDER BY e.joiningDate ASC")
    Page<EmployeeProfile> findSeniorEmployees(Pageable pageable);

    // Employees by years of experience
    @Query("SELECT e FROM EmployeeProfile e WHERE e.yearsOfExperience >= :minExperience " +
           "AND e.status IN ('ACTIVE', 'CONFIRMED', 'ON_PROBATION') " +
           "ORDER BY e.yearsOfExperience DESC")
    List<EmployeeProfile> findEmployeesByExperience(@Param("minExperience") Integer minExperience);

    // Custom validation queries
    @Query("SELECT COUNT(e) > 0 FROM EmployeeProfile e WHERE " +
           "LOWER(e.employeeCode) = LOWER(:employeeCode)")
    boolean existsByEmployeeCodeIgnoreCase(@Param("employeeCode") String employeeCode);

    @Query("SELECT COUNT(e) > 0 FROM EmployeeProfile e WHERE " +
           "LOWER(e.workEmail) = LOWER(:workEmail)")
    boolean existsByWorkEmailIgnoreCase(@Param("workEmail") String workEmail);
}
