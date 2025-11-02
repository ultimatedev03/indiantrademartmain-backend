package com.itech.itech_backend.modules.company.controller;

import com.itech.itech_backend.modules.company.dto.*;
import com.itech.itech_backend.modules.company.model.Company;
import com.itech.itech_backend.modules.company.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {
    
    private final CompanyService companyService;
    
    // CRUD Operations
    @PostMapping
    public ResponseEntity<CompanyDto> createCompany(@Valid @RequestBody CreateCompanyDto createCompanyDto) {
        log.info("REST request to create company: {}", createCompanyDto.getName());
        CompanyDto companyDto = companyService.createCompany(createCompanyDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(companyDto);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable Long id) {
        log.debug("REST request to get company by ID: {}", id);
        CompanyDto companyDto = companyService.getCompanyById(id);
        return ResponseEntity.ok(companyDto);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CompanyDto> updateCompany(@PathVariable Long id, 
                                                   @Valid @RequestBody UpdateCompanyDto updateCompanyDto) {
        log.info("REST request to update company with ID: {}", id);
        CompanyDto companyDto = companyService.updateCompany(id, updateCompanyDto);
        return ResponseEntity.ok(companyDto);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        log.info("REST request to delete company with ID: {}", id);
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
    
    // Company listing and search
    @GetMapping
    public ResponseEntity<Page<CompanyDto>> getAllCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        log.debug("REST request to get all companies - page: {}, size: {}", page, size);
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<CompanyDto> companies = companyService.getAllCompanies(pageRequest);
        return ResponseEntity.ok(companies);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<CompanyDto>> searchCompanies(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        log.debug("REST request to search companies with term: {}", searchTerm);
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<CompanyDto> companies = companyService.searchCompanies(searchTerm, pageRequest);
        return ResponseEntity.ok(companies);
    }
    
    @GetMapping("/filter")
    public ResponseEntity<Page<CompanyDto>> getCompaniesWithFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Company.CompanyType companyType,
            @RequestParam(required = false) Company.BusinessCategory businessCategory,
            @RequestParam(required = false) Company.VerificationStatus verificationStatus,
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(required = false) Company.CompanyStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        log.debug("REST request to get companies with filters");
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<CompanyDto> companies = companyService.getCompaniesWithFilters(
            name, city, state, companyType, businessCategory, verificationStatus, isVerified, status, pageRequest
        );
        return ResponseEntity.ok(companies);
    }
    
    // Company verification
    @PostMapping("/verify")
    public ResponseEntity<CompanyDto> verifyCompany(@Valid @RequestBody CompanyVerificationDto verificationDto) {
        log.info("REST request to verify company with ID: {}", verificationDto.getCompanyId());
        CompanyDto companyDto = companyService.verifyCompany(verificationDto);
        return ResponseEntity.ok(companyDto);
    }
    
    @GetMapping("/verification/pending")
    public ResponseEntity<Page<CompanyDto>> getPendingVerificationCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get pending verification companies");
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<CompanyDto> companies = companyService.getPendingVerificationCompanies(pageRequest);
        return ResponseEntity.ok(companies);
    }
    
    @GetMapping("/verification/{status}")
    public ResponseEntity<Page<CompanyDto>> getCompaniesByVerificationStatus(
            @PathVariable Company.VerificationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get companies by verification status: {}", status);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "verifiedAt"));
        Page<CompanyDto> companies = companyService.getCompaniesByVerificationStatus(status, pageRequest);
        return ResponseEntity.ok(companies);
    }
    
    // Company status management
    @PatchMapping("/{id}/status")
    public ResponseEntity<CompanyDto> updateCompanyStatus(@PathVariable Long id, 
                                                        @RequestParam Company.CompanyStatus status) {
        log.info("REST request to update company status for ID: {} to {}", id, status);
        CompanyDto companyDto = companyService.updateCompanyStatus(id, status);
        return ResponseEntity.ok(companyDto);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<CompanyDto>> getCompaniesByStatus(
            @PathVariable Company.CompanyStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get companies by status: {}", status);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<CompanyDto> companies = companyService.getCompaniesByStatus(status, pageRequest);
        return ResponseEntity.ok(companies);
    }
    
    // Location-based queries
    @GetMapping("/location/city/{city}")
    public ResponseEntity<List<CompanyDto>> getCompaniesByCity(@PathVariable String city) {
        log.debug("REST request to get companies by city: {}", city);
        List<CompanyDto> companies = companyService.getCompaniesByCity(city);
        return ResponseEntity.ok(companies);
    }
    
    @GetMapping("/location/state/{state}")
    public ResponseEntity<List<CompanyDto>> getCompaniesByState(@PathVariable String state) {
        log.debug("REST request to get companies by state: {}", state);
        List<CompanyDto> companies = companyService.getCompaniesByState(state);
        return ResponseEntity.ok(companies);
    }
    
    @GetMapping("/location/{city}/{state}")
    public ResponseEntity<List<CompanyDto>> getCompaniesByCityAndState(@PathVariable String city, 
                                                                      @PathVariable String state) {
        log.debug("REST request to get companies by city: {} and state: {}", city, state);
        List<CompanyDto> companies = companyService.getCompaniesByCityAndState(city, state);
        return ResponseEntity.ok(companies);
    }
    
    // Company categories and types
    @GetMapping("/type/{companyType}")
    public ResponseEntity<Page<CompanyDto>> getCompaniesByType(
            @PathVariable Company.CompanyType companyType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get companies by type: {}", companyType);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<CompanyDto> companies = companyService.getCompaniesByType(companyType, pageRequest);
        return ResponseEntity.ok(companies);
    }
    
    @GetMapping("/category/{businessCategory}")
    public ResponseEntity<Page<CompanyDto>> getCompaniesByBusinessCategory(
            @PathVariable Company.BusinessCategory businessCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get companies by business category: {}", businessCategory);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<CompanyDto> companies = companyService.getCompaniesByBusinessCategory(businessCategory, pageRequest);
        return ResponseEntity.ok(companies);
    }
    
    @GetMapping("/employee-count/{employeeCount}")
    public ResponseEntity<Page<CompanyDto>> getCompaniesByEmployeeCount(
            @PathVariable Company.EmployeeCount employeeCount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get companies by employee count: {}", employeeCount);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<CompanyDto> companies = companyService.getCompaniesByEmployeeCount(employeeCount, pageRequest);
        return ResponseEntity.ok(companies);
    }
    
    @GetMapping("/turnover/{annualTurnover}")
    public ResponseEntity<Page<CompanyDto>> getCompaniesByAnnualTurnover(
            @PathVariable Company.AnnualTurnover annualTurnover,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get companies by annual turnover: {}", annualTurnover);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<CompanyDto> companies = companyService.getCompaniesByAnnualTurnover(annualTurnover, pageRequest);
        return ResponseEntity.ok(companies);
    }
    
    // Premium and subscription management
    @GetMapping("/premium")
    public ResponseEntity<Page<CompanyDto>> getPremiumCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get premium companies");
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "premiumExpiresAt"));
        Page<CompanyDto> companies = companyService.getPremiumCompanies(pageRequest);
        return ResponseEntity.ok(companies);
    }
    
    @GetMapping("/subscription/{subscriptionType}")
    public ResponseEntity<Page<CompanyDto>> getCompaniesBySubscriptionType(
            @PathVariable Company.SubscriptionType subscriptionType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get companies by subscription type: {}", subscriptionType);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<CompanyDto> companies = companyService.getCompaniesBySubscriptionType(subscriptionType, pageRequest);
        return ResponseEntity.ok(companies);
    }
    
    @PostMapping("/{id}/upgrade")
    public ResponseEntity<CompanyDto> upgradeToPremium(@PathVariable Long id, 
                                                     @RequestParam Company.SubscriptionType subscriptionType) {
        log.info("REST request to upgrade company to premium - ID: {}, Subscription: {}", id, subscriptionType);
        CompanyDto companyDto = companyService.upgradeToPremium(id, subscriptionType);
        return ResponseEntity.ok(companyDto);
    }
    
    @PostMapping("/{id}/downgrade")
    public ResponseEntity<CompanyDto> downgradePremium(@PathVariable Long id) {
        log.info("REST request to downgrade company from premium - ID: {}", id);
        CompanyDto companyDto = companyService.downgradePremium(id);
        return ResponseEntity.ok(companyDto);
    }
    
    // Verification and validation
    @GetMapping("/validate/gst/{gstNumber}")
    public ResponseEntity<Boolean> isGstNumberUnique(@PathVariable String gstNumber) {
        log.debug("REST request to validate GST number uniqueness: {}", gstNumber);
        boolean isUnique = companyService.isGstNumberUnique(gstNumber);
        return ResponseEntity.ok(isUnique);
    }
    
    @GetMapping("/validate/pan/{panNumber}")
    public ResponseEntity<Boolean> isPanNumberUnique(@PathVariable String panNumber) {
        log.debug("REST request to validate PAN number uniqueness: {}", panNumber);
        boolean isUnique = companyService.isPanNumberUnique(panNumber);
        return ResponseEntity.ok(isUnique);
    }
    
    @GetMapping("/validate/cin/{cinNumber}")
    public ResponseEntity<Boolean> isCinNumberUnique(@PathVariable String cinNumber) {
        log.debug("REST request to validate CIN number uniqueness: {}", cinNumber);
        boolean isUnique = companyService.isCinNumberUnique(cinNumber);
        return ResponseEntity.ok(isUnique);
    }
    
    @GetMapping("/validate/email/{email}")
    public ResponseEntity<Boolean> isEmailUnique(@PathVariable String email) {
        log.debug("REST request to validate email uniqueness: {}", email);
        boolean isUnique = companyService.isEmailUnique(email);
        return ResponseEntity.ok(isUnique);
    }
    
    @GetMapping("/lookup/gst/{gstNumber}")
    public ResponseEntity<CompanyDto> getCompanyByGstNumber(@PathVariable String gstNumber) {
        log.debug("REST request to get company by GST number: {}", gstNumber);
        CompanyDto companyDto = companyService.getCompanyByGstNumber(gstNumber);
        return ResponseEntity.ok(companyDto);
    }
    
    @GetMapping("/lookup/pan/{panNumber}")
    public ResponseEntity<CompanyDto> getCompanyByPanNumber(@PathVariable String panNumber) {
        log.debug("REST request to get company by PAN number: {}", panNumber);
        CompanyDto companyDto = companyService.getCompanyByPanNumber(panNumber);
        return ResponseEntity.ok(companyDto);
    }
    
    @GetMapping("/lookup/email/{email}")
    public ResponseEntity<CompanyDto> getCompanyByEmail(@PathVariable String email) {
        log.debug("REST request to get company by email: {}", email);
        CompanyDto companyDto = companyService.getCompanyByEmail(email);
        return ResponseEntity.ok(companyDto);
    }
    
    // Statistics and analytics
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getCompanyStatistics() {
        log.debug("REST request to get company statistics");
        Map<String, Long> stats = companyService.getCompanyStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/statistics/count")
    public ResponseEntity<Long> getTotalCompaniesCount() {
        log.debug("REST request to get total companies count");
        long count = companyService.getTotalCompaniesCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/statistics/verified")
    public ResponseEntity<Long> getVerifiedCompaniesCount() {
        log.debug("REST request to get verified companies count");
        long count = companyService.getVerifiedCompaniesCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/statistics/premium")
    public ResponseEntity<Long> getPremiumCompaniesCount() {
        log.debug("REST request to get premium companies count");
        long count = companyService.getPremiumCompaniesCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/statistics/verification-status")
    public ResponseEntity<Map<Company.VerificationStatus, Long>> getCompanyCountByVerificationStatus() {
        log.debug("REST request to get company count by verification status");
        Map<Company.VerificationStatus, Long> counts = companyService.getCompanyCountByVerificationStatus();
        return ResponseEntity.ok(counts);
    }
    
    @GetMapping("/statistics/status")
    public ResponseEntity<Map<Company.CompanyStatus, Long>> getCompanyCountByStatus() {
        log.debug("REST request to get company count by status");
        Map<Company.CompanyStatus, Long> counts = companyService.getCompanyCountByStatus();
        return ResponseEntity.ok(counts);
    }
    
    @GetMapping("/statistics/type")
    public ResponseEntity<Map<Company.CompanyType, Long>> getCompanyCountByType() {
        log.debug("REST request to get company count by type");
        Map<Company.CompanyType, Long> counts = companyService.getCompanyCountByType();
        return ResponseEntity.ok(counts);
    }
    
    @GetMapping("/statistics/category")
    public ResponseEntity<Map<Company.BusinessCategory, Long>> getCompanyCountByCategory() {
        log.debug("REST request to get company count by category");
        Map<Company.BusinessCategory, Long> counts = companyService.getCompanyCountByCategory();
        return ResponseEntity.ok(counts);
    }
    
    // Recent and trending companies
    @GetMapping("/recent")
    public ResponseEntity<Page<CompanyDto>> getRecentCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get recent companies");
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<CompanyDto> companies = companyService.getRecentCompanies(pageRequest);
        return ResponseEntity.ok(companies);
    }
    
    @GetMapping("/top-premium")
    public ResponseEntity<Page<CompanyDto>> getTopVerifiedPremiumCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.debug("REST request to get top verified premium companies");
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<CompanyDto> companies = companyService.getTopVerifiedPremiumCompanies(pageRequest);
        return ResponseEntity.ok(companies);
    }
    
    // Bulk operations
    @PostMapping("/bulk/create")
    public ResponseEntity<List<CompanyDto>> createCompaniesInBulk(@Valid @RequestBody List<CreateCompanyDto> createCompanyDtos) {
        log.info("REST request to create {} companies in bulk", createCompanyDtos.size());
        List<CompanyDto> createdCompanies = companyService.createCompaniesInBulk(createCompanyDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCompanies);
    }
    
    @PostMapping("/bulk/status")
    public ResponseEntity<Map<String, Object>> bulkUpdateCompanyStatus(@RequestBody List<Long> companyIds, 
                                                                      @RequestParam Company.CompanyStatus status) {
        log.info("REST request to bulk update company status for {} companies to {}", companyIds.size(), status);
        Map<String, Object> result = companyService.bulkUpdateCompanyStatus(companyIds, status);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/bulk/verify")
    public ResponseEntity<Map<String, Object>> bulkVerifyCompanies(@RequestBody List<Long> companyIds, 
                                                                  @RequestParam Company.VerificationStatus status) {
        log.info("REST request to bulk verify {} companies with status {}", companyIds.size(), status);
        Map<String, Object> result = companyService.bulkVerifyCompanies(companyIds, status);
        return ResponseEntity.ok(result);
    }
    
    // Export functionality
    @PostMapping("/export/excel")
    public ResponseEntity<byte[]> exportCompaniesToExcel(@RequestBody List<Long> companyIds) {
        log.info("REST request to export {} companies to Excel", companyIds.size());
        byte[] excelData = companyService.exportCompaniesToExcel(companyIds);
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=companies.xlsx")
            .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            .body(excelData);
    }
    
    @PostMapping("/export/pdf")
    public ResponseEntity<byte[]> exportCompaniesToPdf(@RequestBody List<Long> companyIds) {
        log.info("REST request to export {} companies to PDF", companyIds.size());
        byte[] pdfData = companyService.exportCompaniesToPdf(companyIds);
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=companies.pdf")
            .header("Content-Type", "application/pdf")
            .body(pdfData);
    }
    
    // Company profile management
    @PatchMapping("/{id}/logo")
    public ResponseEntity<CompanyDto> updateCompanyLogo(@PathVariable Long id, 
                                                       @RequestParam String logoUrl) {
        log.info("REST request to update logo for company ID: {}", id);
        CompanyDto companyDto = companyService.updateCompanyLogo(id, logoUrl);
        return ResponseEntity.ok(companyDto);
    }
    
    @PostMapping("/{id}/images")
    public ResponseEntity<CompanyDto> addCompanyImages(@PathVariable Long id, 
                                                      @RequestBody List<String> imageUrls) {
        log.info("REST request to add {} images for company ID: {}", imageUrls.size(), id);
        CompanyDto companyDto = companyService.addCompanyImages(id, imageUrls);
        return ResponseEntity.ok(companyDto);
    }
    
    @DeleteMapping("/{id}/images")
    public ResponseEntity<CompanyDto> removeCompanyImage(@PathVariable Long id, 
                                                        @RequestParam String imageUrl) {
        log.info("REST request to remove image for company ID: {}", id);
        CompanyDto companyDto = companyService.removeCompanyImage(id, imageUrl);
        return ResponseEntity.ok(companyDto);
    }
    
    @PatchMapping("/{id}/banking")
    public ResponseEntity<CompanyDto> updateBankingInfo(@PathVariable Long id,
                                                       @RequestParam String bankName,
                                                       @RequestParam String accountNumber,
                                                       @RequestParam String ifscCode,
                                                       @RequestParam String accountHolderName) {
        log.info("REST request to update banking info for company ID: {}", id);
        CompanyDto companyDto = companyService.updateBankingInfo(id, bankName, accountNumber, ifscCode, accountHolderName);
        return ResponseEntity.ok(companyDto);
    }
}

