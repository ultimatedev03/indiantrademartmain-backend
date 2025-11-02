package com.itech.itech_backend.controller;

import com.itech.itech_backend.modules.shared.dto.*;
import com.itech.itech_backend.modules.payment.model.*;
import com.itech.itech_backend.modules.shared.service.*;
import com.itech.itech_backend.modules.payment.service.InvoiceService;
import com.itech.itech_backend.modules.payment.service.PaymentService;
import com.itech.marketplace.model.VendorPayment;
import jakarta.validation.Valid;

import com.itech.marketplace.dto.*;
import com.itech.marketplace.entity.*;
import com.itech.marketplace.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FinanceController {

    private final InvoiceService invoiceService;
    private final com.itech.marketplace.service.RefundService refundService;
private final com.itech.marketplace.service.TransactionService transactionService;
    private final PaymentService paymentService;

    // Invoice Management
    @GetMapping("/invoices")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<Page<Invoice>> getAllInvoices(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long vendorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        
        Page<Invoice> invoices = invoiceService.getInvoices(status, vendorId, startDate, endDate, pageable);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/invoices/{invoiceId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE') or @invoiceService.isInvoiceOwner(#invoiceId, authentication.name)")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long invoiceId) {
        Invoice invoice = invoiceService.getInvoiceById(invoiceId);
        return ResponseEntity.ok(invoice);
    }

    @PostMapping("/invoices/generate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<Invoice> generateInvoice(@Valid @RequestBody com.itech.itech_backend.modules.payment.dto.InvoiceGenerationDto request) {
        Invoice invoice = invoiceService.generateInvoice(request);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/invoices/{invoiceId}/download")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE') or @invoiceService.isInvoiceOwner(#invoiceId, authentication.name)")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long invoiceId) {
        byte[] pdfContent = invoiceService.generateInvoicePdf(invoiceId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice-" + invoiceId + ".pdf");
        
        return ResponseEntity.ok().headers(headers).body(pdfContent);
    }

    @PutMapping("/invoices/{invoiceId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<Invoice> updateInvoiceStatus(
            @PathVariable Long invoiceId,
            @RequestBody Map<String, String> statusUpdate) {
        
        Invoice invoice = invoiceService.updateInvoiceStatus(invoiceId, statusUpdate.get("status"));
        return ResponseEntity.ok(invoice);
    }

    // Refund Management
    @GetMapping("/refunds")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<Page<Refund>> getAllRefunds(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        
        Page<Refund> refunds = refundService.getRefunds(status, orderId, startDate, endDate, pageable);
        return ResponseEntity.ok(refunds);
    }

    @GetMapping("/refunds/{refundId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE') or @refundService.isRefundOwner(#refundId, authentication.name)")
    public ResponseEntity<Refund> getRefund(@PathVariable Long refundId) {
        Refund refund = refundService.getRefundById(refundId);
        return ResponseEntity.ok(refund);
    }

    @PostMapping("/refunds/initiate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<Refund> initiateRefund(@Valid @RequestBody RefundRequestDto request) {
        Refund refund = refundService.initiateRefund(request);
        return ResponseEntity.ok(refund);
    }

    @PutMapping("/refunds/{refundId}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<Refund> approveRefund(@PathVariable Long refundId) {
        Refund refund = refundService.approveRefund(refundId);
        return ResponseEntity.ok(refund);
    }

    @PutMapping("/refunds/{refundId}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<Refund> rejectRefund(
            @PathVariable Long refundId,
            @RequestBody Map<String, String> rejectionReason) {
        
        Refund refund = refundService.rejectRefund(refundId, rejectionReason.get("reason"));
        return ResponseEntity.ok(refund);
    }

    @PutMapping("/refunds/{refundId}/process")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<Refund> processRefund(@PathVariable Long refundId) {
        Refund refund = refundService.processRefund(refundId);
        return ResponseEntity.ok(refund);
    }

    // Transaction Management
    @GetMapping("/transactions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<Page<Transaction>> getAllTransactions(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        
        Page<Transaction> transactions = transactionService.getTransactions(
            type, status, userId, startDate, endDate, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions/{transactionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE') or @transactionService.isTransactionOwner(#transactionId, authentication.name)")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long transactionId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(transaction);
    }

    // Financial Reports
    @GetMapping("/reports/revenue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<com.itech.itech_backend.modules.payment.dto.FinancialReportDto> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "DAILY") String groupBy) {
        
        com.itech.itech_backend.modules.payment.dto.FinancialReportDto report = invoiceService.generateRevenueReport(startDate, endDate, groupBy);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/reports/subscription")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<com.itech.itech_backend.modules.payment.dto.SubscriptionReportDto> getSubscriptionReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        com.itech.itech_backend.modules.payment.dto.SubscriptionReportDto report = invoiceService.generateSubscriptionReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/reports/payment-summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<PaymentSummaryDto> getPaymentSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        PaymentSummaryDto summary = paymentService.getPaymentSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/reports/refund-analytics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<RefundAnalyticsDto> getRefundAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        RefundAnalyticsDto analytics = refundService.getRefundAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    // Dashboard Overview
    @GetMapping("/dashboard/overview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<com.itech.itech_backend.modules.payment.dto.FinanceDashboardDto> getDashboardOverview() {
        com.itech.itech_backend.modules.payment.dto.FinanceDashboardDto overview = invoiceService.getDashboardOverview();
        return ResponseEntity.ok(overview);
    }

    // GST and Tax Reports
    @GetMapping("/reports/gst")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<com.itech.itech_backend.modules.payment.dto.GSTReportDto> getGSTReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        com.itech.itech_backend.modules.payment.dto.GSTReportDto gstReport = invoiceService.generateGSTReport(startDate, endDate);
        return ResponseEntity.ok(gstReport);
    }

    @PostMapping("/invoices/bulk-generate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<List<Invoice>> bulkGenerateInvoices(@Valid @RequestBody com.itech.itech_backend.modules.payment.dto.BulkInvoiceGenerationDto request) {
        List<Invoice> invoices = invoiceService.bulkGenerateInvoices(request);
        return ResponseEntity.ok(invoices);
    }

    // Vendor Payments
    @GetMapping("/vendor-payments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<Page<VendorPayment>> getVendorPayments(
            @RequestParam(required = false) Long vendorId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        
        Page<VendorPayment> payments = paymentService.getVendorPayments(
            vendorId, status, startDate, endDate, pageable);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/vendor-payments/process")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<VendorPayment> processVendorPayment(@Valid @RequestBody VendorPaymentDto request) {
        VendorPayment payment = paymentService.processVendorPayment(request);
        return ResponseEntity.ok(payment);
    }
}

