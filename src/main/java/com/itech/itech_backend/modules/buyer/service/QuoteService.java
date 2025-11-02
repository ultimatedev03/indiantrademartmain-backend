package com.itech.itech_backend.modules.buyer.service;

import com.itech.itech_backend.modules.shared.dto.CreateQuoteDto;
import com.itech.itech_backend.modules.buyer.model.Inquiry;
import com.itech.itech_backend.modules.buyer.model.Quote;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.buyer.repository.InquiryRepository;
import com.itech.itech_backend.modules.buyer.repository.QuoteRepository;
import com.itech.itech_backend.modules.vendor.repository.VendorsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final VendorsRepository vendorsRepository;
    private final InquiryRepository inquiryRepository;

    public Quote createQuote(CreateQuoteDto dto) {
        Optional<Vendors> vendorOpt = vendorsRepository.findById(dto.getVendorId());
        Optional<Inquiry> inquiryOpt = inquiryRepository.findById(dto.getInquiryId());

        if (vendorOpt.isEmpty() || inquiryOpt.isEmpty()) {
            throw new IllegalArgumentException("Vendor or Inquiry not found");
        }

        Quote quote = Quote.builder()
                .vendor(vendorOpt.get())
                .inquiry(inquiryOpt.get())
                .response(dto.getResponse())
                .createdAt(LocalDateTime.now())
                .build();

        return quoteRepository.save(quote);
    }

    public List<Quote> getAllQuotes() {
        return quoteRepository.findAll();
    }

    public List<Quote> getQuotesByVendor(Long vendorId) {
        return quoteRepository.findByVendorId(vendorId);
    }

    public List<Quote> getQuotesForInquiry(Long inquiryId) {
        return quoteRepository.findByInquiryId(inquiryId);
    }

    public List<Quote> getQuotesForUser(Long userId) {
        return quoteRepository.findByInquiryUserId(userId);
    }

    public Quote acceptQuote(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("Quote not found"));

        quote.setAccepted(true);
        return quoteRepository.save(quote);
    }

    public void deleteQuote(Long quoteId) {
        quoteRepository.deleteById(quoteId);
    }
    
    // Additional methods with Pageable support
    public Page<Quote> getQuotesByVendor(Long vendorId, Pageable pageable) {
        return quoteRepository.findByVendorIdOrderByCreatedAtDesc(vendorId, pageable);
    }
    
    public Page<Quote> getQuotesByUser(Long userId, Pageable pageable) {
        return quoteRepository.findByInquiryUserIdOrderByCreatedAtDesc(userId, pageable);
    }
}

