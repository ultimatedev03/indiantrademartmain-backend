package com.itech.itech_backend.modules.payment.service;

import com.itech.itech_backend.modules.payment.model.*;
import com.itech.itech_backend.modules.payment.dto.*;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.payment.repository.InvoiceRepository;
import com.itech.itech_backend.modules.vendor.repository.VendorsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final VendorsRepository vendorsRepository;

    private static final String COMPANY_GST = "27AALCS6669K1ZF"; // Company GST number
    private static final BigDecimal GST_RATE = new BigDecimal("18.00"); // 18% GST

    /**
     * Generate GST invoice for subscription payment
     */
    @Transactional
    public Invoice generateSubscriptionInvoice(Payment payment, Subscription subscription) {
        log.info("Generating invoice for payment: {}", payment.getId());

        // TODO: Fix vendor relationship - Payment model doesn't have direct vendor field
        // For now, we'll need to get vendor from a different source
        Vendors vendor = null; // This needs to be fixed based on actual data model relationships
        BigDecimal subtotal = payment.getAmount();
        
        // Calculate GST amounts
        String vendorGstNumber = vendor != null ? vendor.getGstNumber() : null;
        Map<String, BigDecimal> gstAmounts = calculateGST(subtotal, vendorGstNumber);
        
        String invoiceNumber = generateInvoiceNumber();
        
        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .vendor(vendor)
                .payment(payment)
                .subscription(subscription)
                .subtotal(subtotal)
                .cgstAmount(gstAmounts.get("cgst"))
                .sgstAmount(gstAmounts.get("sgst"))
                .igstAmount(gstAmounts.get("igst"))
                .totalAmount(gstAmounts.get("total"))
                .cgstRate(gstAmounts.get("cgstRate"))
                .sgstRate(gstAmounts.get("sgstRate"))
                .igstRate(gstAmounts.get("igstRate"))
                .vendorGstNumber(vendor != null ? vendor.getGstNumber() : null)
                .companyGstNumber(COMPANY_GST)
                .status(Invoice.InvoiceStatus.GENERATED)
                .type(Invoice.InvoiceType.SUBSCRIPTION)
                .description("Subscription: " + subscription.getPlanName())
                .billingAddress(vendor != null ? vendor.getBusinessAddress() : null)
                .shippingAddress(vendor != null ? vendor.getBusinessAddress() : null)
                .dueDate(LocalDateTime.now().plusDays(30))
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice generated successfully: {}", invoiceNumber);
        
        return savedInvoice;
    }

    /**
     * Calculate GST amounts based on vendor location
     */
    private Map<String, BigDecimal> calculateGST(BigDecimal subtotal, String vendorGstNumber) {
        Map<String, BigDecimal> gstAmounts = new HashMap<>();
        
        boolean isInterState = isInterStateTransaction(vendorGstNumber);
        
        if (isInterState) {
            // Inter-state: IGST 18%
            BigDecimal igstAmount = subtotal.multiply(GST_RATE).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            gstAmounts.put("igst", igstAmount);
            gstAmounts.put("cgst", BigDecimal.ZERO);
            gstAmounts.put("sgst", BigDecimal.ZERO);
            gstAmounts.put("igstRate", GST_RATE);
            gstAmounts.put("cgstRate", BigDecimal.ZERO);
            gstAmounts.put("sgstRate", BigDecimal.ZERO);
            gstAmounts.put("total", subtotal.add(igstAmount));
        } else {
            // Intra-state: CGST 9% + SGST 9%
            BigDecimal halfGstRate = GST_RATE.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
            BigDecimal cgstAmount = subtotal.multiply(halfGstRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal sgstAmount = subtotal.multiply(halfGstRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            
            gstAmounts.put("cgst", cgstAmount);
            gstAmounts.put("sgst", sgstAmount);
            gstAmounts.put("igst", BigDecimal.ZERO);
            gstAmounts.put("cgstRate", halfGstRate);
            gstAmounts.put("sgstRate", halfGstRate);
            gstAmounts.put("igstRate", BigDecimal.ZERO);
            gstAmounts.put("total", subtotal.add(cgstAmount).add(sgstAmount));
        }
        
        return gstAmounts;
    }

    /**
     * Check if transaction is inter-state based on GST numbers
     */
    private boolean isInterStateTransaction(String vendorGstNumber) {
        if (vendorGstNumber == null || vendorGstNumber.length() < 2) {
            return true; // Default to inter-state if GST not available
        }
        
        String vendorStateCode = vendorGstNumber.substring(0, 2);
        String companyStateCode = COMPANY_GST.substring(0, 2);
        
        return !vendorStateCode.equals(companyStateCode);
    }

    /**
     * Generate unique invoice number
     */
    private String generateInvoiceNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "INV-" + timestamp + "-" + uuid;
    }

    /**
     * Mark invoice as paid
     */
    @Transactional
    public Invoice markAsPaid(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        invoice.setStatus(Invoice.InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());
        
        return invoiceRepository.save(invoice);
    }

    /**
     * Get invoices by vendor
     */
    public List<Invoice> getVendorInvoices(Vendors vendor) {
        return invoiceRepository.findByVendorOrderByCreatedAtDesc(vendor);
    }

    /**
     * Get paginated invoices by vendor
     */
    public Page<Invoice> getVendorInvoices(Vendors vendor, Pageable pageable) {
        return invoiceRepository.findByVendorOrderByCreatedAtDesc(vendor, pageable);
    }

    /**
     * Get all invoices for admin
     */
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    /**
     * Get invoices by status
     */
    public List<Invoice> getInvoicesByStatus(Invoice.InvoiceStatus status) {
        return invoiceRepository.findByStatus(status);
    }

    /**
     * Get invoices by date range
     */
    public List<Invoice> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return invoiceRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Get invoice analytics
     */
    public Map<String, Object> getInvoiceAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Status counts
        List<Object[]> statusCounts = invoiceRepository.getInvoiceStatusCounts();
        Map<String, Long> statusAnalytics = new HashMap<>();
        for (Object[] row : statusCounts) {
            statusAnalytics.put(row[0].toString(), (Long) row[1]);
        }
        analytics.put("statusCounts", statusAnalytics);
        
        // Total amounts
        BigDecimal totalPaid = invoiceRepository.getTotalPaidAmount();
        analytics.put("totalPaidAmount", totalPaid != null ? totalPaid : BigDecimal.ZERO);
        
        // Revenue by type
        List<Object[]> revenueByType = invoiceRepository.getRevenueByType();
        Map<String, Map<String, Object>> revenueAnalytics = new HashMap<>();
        for (Object[] row : revenueByType) {
            Map<String, Object> typeData = new HashMap<>();
            typeData.put("count", row[1]);
            typeData.put("total", row[2]);
            revenueAnalytics.put(row[0].toString(), typeData);
        }
        analytics.put("revenueByType", revenueAnalytics);
        
        // Daily revenue (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Object[]> dailyRevenue = invoiceRepository.getDailyRevenue(thirtyDaysAgo);
        analytics.put("dailyRevenue", dailyRevenue);
        
        return analytics;
    }

    /**
     * Generate PDF invoice (placeholder - integrate with PDF library)
     */
    public byte[] generateInvoicePDF(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        // TODO: Implement PDF generation using iText or similar library
        // This is a placeholder implementation
        log.info("Generating PDF for invoice: {}", invoice.getInvoiceNumber());
        
        String pdfContent = generateInvoiceHTML(invoice);
        return pdfContent.getBytes(); // In real implementation, convert HTML to PDF
    }

    /**
     * Generate HTML content for invoice
     */
    private String generateInvoiceHTML(Invoice invoice) {
        return String.format("""
            <html>
            <head><title>Invoice %s</title></head>
            <body>
                <h1>GST INVOICE</h1>
                <h2>Invoice Number: %s</h2>
                <p>Date: %s</p>
                
                <h3>From:</h3>
                <p>Your Company Name<br/>
                GST: %s</p>
                
                <h3>To:</h3>
                <p>%s<br/>
                GST: %s<br/>
                %s</p>
                
                <table border="1">
                    <tr><th>Description</th><th>Amount</th></tr>
                    <tr><td>%s</td><td>₹%.2f</td></tr>
                    <tr><td>CGST (%.2f%%)</td><td>₹%.2f</td></tr>
                    <tr><td>SGST (%.2f%%)</td><td>₹%.2f</td></tr>
                    <tr><td>IGST (%.2f%%)</td><td>₹%.2f</td></tr>
                    <tr><th>Total</th><th>₹%.2f</th></tr>
                </table>
            </body>
            </html>
            """,
            invoice.getInvoiceNumber(),
            invoice.getInvoiceNumber(),
            invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            invoice.getCompanyGstNumber(),
            invoice.getVendor() != null ? invoice.getVendor().getBusinessName() : "N/A",
            invoice.getVendorGstNumber(),
            invoice.getBillingAddress(),
            invoice.getDescription(),
            invoice.getSubtotal(),
            invoice.getCgstRate(),
            invoice.getCgstAmount(),
            invoice.getSgstRate(),
            invoice.getSgstAmount(),
            invoice.getIgstRate(),
            invoice.getIgstAmount(),
            invoice.getTotalAmount()
        );
    }

    // Additional methods required by FinanceController
    public Page<Invoice> getInvoices(String status, Long vendorId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        // Implementation for filtering invoices
        return invoiceRepository.findAll(pageable); // Simplified implementation
    }

    public Invoice getInvoiceById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    public boolean isInvoiceOwner(Long invoiceId, String username) {
        // Check if the authenticated user owns this invoice
        return true; // Placeholder implementation
    }

    public Invoice generateInvoice(InvoiceGenerationDto request) {
        // Generate invoice from DTO
        throw new RuntimeException("Not implemented yet");
    }

    public byte[] generateInvoicePdf(Long invoiceId) {
        return generateInvoicePDF(invoiceId);
    }

    public Invoice updateInvoiceStatus(Long invoiceId, String status) {
        Invoice invoice = getInvoiceById(invoiceId);
        invoice.setStatus(Invoice.InvoiceStatus.valueOf(status));
        return invoiceRepository.save(invoice);
    }

    public FinancialReportDto generateRevenueReport(LocalDateTime startDate, LocalDateTime endDate, String groupBy) {
        // Generate financial report
        throw new RuntimeException("Not implemented yet");
    }

    public SubscriptionReportDto generateSubscriptionReport(LocalDateTime startDate, LocalDateTime endDate) {
        // Generate subscription report
        throw new RuntimeException("Not implemented yet");
    }

    public FinanceDashboardDto getDashboardOverview() {
        // Generate dashboard overview
        throw new RuntimeException("Not implemented yet");
    }

    public GSTReportDto generateGSTReport(LocalDateTime startDate, LocalDateTime endDate) {
        // Generate GST report
        throw new RuntimeException("Not implemented yet");
    }

    public List<Invoice> bulkGenerateInvoices(BulkInvoiceGenerationDto request) {
        // Bulk generate invoices
        throw new RuntimeException("Not implemented yet");
    }
}


