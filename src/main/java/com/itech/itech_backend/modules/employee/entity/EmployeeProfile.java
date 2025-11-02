package com.itech.itech_backend.modules.employee.entity;

import com.itech.itech_backend.modules.core.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "employee_profiles",
       indexes = {
           @Index(name = "idx_employee_code", columnList = "employeeCode"),
           @Index(name = "idx_department", columnList = "department"),
           @Index(name = "idx_designation", columnList = "designation"),
           @Index(name = "idx_status", columnList = "status"),
           @Index(name = "idx_manager_id", columnList = "managerId"),
           @Index(name = "idx_joining_date", columnList = "joiningDate")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_employee_code", columnNames = {"employeeCode"}),
           @UniqueConstraint(name = "uk_employee_email", columnNames = {"workEmail"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to User entity
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @NotBlank(message = "Employee code is required")
    @Size(max = 50, message = "Employee code must not exceed 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String employeeCode;

    // Personal Information
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String lastName;

    @Size(max = 50, message = "Middle name must not exceed 50 characters")
    @Column(length = 50)
    private String middleName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    // Contact Information
    @Email(message = "Please provide a valid work email")
    @NotBlank(message = "Work email is required")
    @Size(max = 100, message = "Work email must not exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String workEmail;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Please provide a valid phone number")
    @Column(length = 20)
    private String workPhone;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Please provide a valid mobile number")
    @Column(length = 20)
    private String personalMobile;

    @Email(message = "Please provide a valid personal email")
    @Size(max = 100, message = "Personal email must not exceed 100 characters")
    @Column(length = 100)
    private String personalEmail;

    // Address Information
    @Column(columnDefinition = "TEXT")
    private String currentAddress;

    @Column(columnDefinition = "TEXT")
    private String permanentAddress;

    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(length = 100)
    private String city;

    @Size(max = 100, message = "State must not exceed 100 characters")
    @Column(length = 100)
    private String state;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Column(length = 100)
    private String country;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    @Column(length = 20)
    private String postalCode;

    // Employment Information
    @NotBlank(message = "Department is required")
    @Size(max = 100, message = "Department must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String department;

    @NotBlank(message = "Designation is required")
    @Size(max = 100, message = "Designation must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String designation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EmployeeType employeeType = EmployeeType.FULL_TIME;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    @NotNull(message = "Joining date is required")
    @PastOrPresent(message = "Joining date must be today or in the past")
    @Column(nullable = false)
    private LocalDate joiningDate;

    private LocalDate probationEndDate;

    private LocalDate confirmationDate;

    private LocalDate resignationDate;

    private LocalDate lastWorkingDay;

    // Reporting Structure
    @Column(name = "manager_id")
    private Long managerId;

    @Transient
    private EmployeeProfile manager;

    // Work Location
    @Size(max = 100, message = "Work location must not exceed 100 characters")
    @Column(length = 100)
    private String workLocation;

    @Size(max = 50, message = "Floor/Cabin must not exceed 50 characters")
    @Column(length = 50)
    private String floorCabin;

    // Emergency Contact
    @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
    @Column(length = 100)
    private String emergencyContactName;

    @Size(max = 50, message = "Emergency contact relation must not exceed 50 characters")
    @Column(length = 50)
    private String emergencyContactRelation;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Please provide a valid emergency contact number")
    @Column(length = 20)
    private String emergencyContactPhone;

    // Skills and Qualifications
    @Column(columnDefinition = "TEXT")
    private String skills;

    @Size(max = 200, message = "Qualification must not exceed 200 characters")
    @Column(length = 200)
    private String qualification;

    @Column(name = "years_of_experience")
    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 50, message = "Years of experience cannot exceed 50")
    private Integer yearsOfExperience;

    // Documents
    @Size(max = 20, message = "PAN number must not exceed 20 characters")
    @Column(length = 20)
    private String panNumber;

    @Size(max = 20, message = "Aadhaar number must not exceed 20 characters")
    @Column(length = 20)
    private String aadhaarNumber;

    @Size(max = 20, message = "Passport number must not exceed 20 characters")
    @Column(length = 20)
    private String passportNumber;

    // Bank Details
    @Size(max = 100, message = "Bank name must not exceed 100 characters")
    @Column(length = 100)
    private String bankName;

    @Size(max = 50, message = "Account number must not exceed 50 characters")
    @Column(length = 50)
    private String accountNumber;

    @Size(max = 20, message = "IFSC code must not exceed 20 characters")
    @Column(length = 20)
    private String ifscCode;

    // Profile and Bio
    @Size(max = 500, message = "Bio must not exceed 500 characters")
    @Column(length = 500)
    private String bio;

    @Size(max = 255, message = "Profile picture URL must not exceed 255 characters")
    @Column(length = 255)
    private String profilePictureUrl;

    // System fields
    @Column(columnDefinition = "TEXT")
    private String notes; // Internal notes by HR/Admin

    @Column(nullable = false)
    @Builder.Default
    private Boolean isProfileComplete = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDocumentsVerified = false;

    // Access Controls
    @ElementCollection(targetClass = EmployeeRole.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "employee_roles", 
                    joinColumns = @JoinColumn(name = "employee_id"),
                    indexes = @Index(name = "idx_employee_roles", columnList = "employee_id"))
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<EmployeeRole> roles = Set.of(EmployeeRole.EMPLOYEE);

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "employee_permissions", 
                    joinColumns = @JoinColumn(name = "employee_id"),
                    indexes = @Index(name = "idx_employee_permissions", columnList = "employee_id"))
    @Column(name = "permission")
    @Builder.Default
    private Set<String> permissions = Set.of();

    // Audit fields
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by_id")
    private Long createdById;

    @Column(name = "updated_by_id")
    private Long updatedById;

    // Enums
    public enum Gender {
        MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY
    }

    public enum EmployeeType {
        FULL_TIME, PART_TIME, CONTRACT, INTERN, FREELANCER
    }

    public enum EmployeeStatus {
        ACTIVE, INACTIVE, ON_PROBATION, CONFIRMED, NOTICE_PERIOD, RESIGNED, TERMINATED
    }

    public enum EmployeeRole {
        EMPLOYEE, TEAM_LEAD, SUPERVISOR, MANAGER, SENIOR_MANAGER, 
        DIRECTOR, HR, ADMIN, SUPER_ADMIN
    }

    // Lifecycle callbacks
    @PrePersist
    @PreUpdate
    private void validateAndNormalize() {
        // Normalize data
        if (firstName != null) firstName = firstName.trim();
        if (lastName != null) lastName = lastName.trim();
        if (middleName != null) middleName = middleName.trim();
        if (workEmail != null) workEmail = workEmail.toLowerCase().trim();
        if (personalEmail != null) personalEmail = personalEmail.toLowerCase().trim();
        if (department != null) department = department.trim();
        if (designation != null) designation = designation.trim();
        
        // Check profile completeness
        updateProfileCompleteness();
    }

    // Utility methods
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        fullName.append(firstName);
        if (middleName != null && !middleName.trim().isEmpty()) {
            fullName.append(" ").append(middleName);
        }
        fullName.append(" ").append(lastName);
        return fullName.toString();
    }

    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return status == EmployeeStatus.ACTIVE || status == EmployeeStatus.CONFIRMED;
    }

    public boolean isOnProbation() {
        return status == EmployeeStatus.ON_PROBATION;
    }

    public boolean hasRole(EmployeeRole role) {
        return roles != null && roles.contains(role);
    }

    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }

    public int getWorkExperienceInCompany() {
        if (joiningDate == null) return 0;
        return (int) joiningDate.until(LocalDate.now()).getYears();
    }

    private void updateProfileCompleteness() {
        // Check if essential fields are filled
        boolean isComplete = firstName != null && !firstName.trim().isEmpty()
                && lastName != null && !lastName.trim().isEmpty()
                && workEmail != null && !workEmail.trim().isEmpty()
                && department != null && !department.trim().isEmpty()
                && designation != null && !designation.trim().isEmpty()
                && joiningDate != null
                && workPhone != null && !workPhone.trim().isEmpty()
                && currentAddress != null && !currentAddress.trim().isEmpty();
        
        this.isProfileComplete = isComplete;
    }

    @Override
    public String toString() {
        return "EmployeeProfile{" +
                "id=" + id +
                ", employeeCode='" + employeeCode + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", department='" + department + '\'' +
                ", designation='" + designation + '\'' +
                ", status=" + status +
                ", joiningDate=" + joiningDate +
                '}';
    }
}
