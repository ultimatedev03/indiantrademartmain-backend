package com.itech.itech_backend.modules.buyer.controller;

import com.itech.itech_backend.modules.shared.dto.CreateQuoteDto;
import com.itech.itech_backend.modules.buyer.model.Quote;
import com.itech.itech_backend.modules.buyer.service.QuoteService;
import com.itech.itech_backend.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
@Slf4j
public class QuoteController {

    private final QuoteService quoteService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> createQuote(@RequestBody CreateQuoteDto quoteDto, HttpServletRequest request) {
        try {
            Long vendorId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (vendorId == null) {
                return ResponseEntity.badRequest().body("Vendor not authenticated");
            }
            // Set vendorId in the DTO
            quoteDto.setVendorId(vendorId);
            Quote createdQuote = quoteService.createQuote(quoteDto);
            return ResponseEntity.ok(createdQuote);

        } catch (Exception e) {
            log.error("Error creating quote", e);
            return ResponseEntity.badRequest().body("Failed to create quote: " + e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Quote>> getAllQuotes() {
        List<Quote> quotes = quoteService.getAllQuotes();
        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/my-quotes")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> getMyQuotes(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        HttpServletRequest request) {
        try {
            Long vendorId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (vendorId == null) {
                return ResponseEntity.badRequest().body("Vendor not authenticated");
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Quote> quotes = quoteService.getQuotesByVendor(vendorId, pageable);
            return ResponseEntity.ok(quotes);

        } catch (Exception e) {
            log.error("Error fetching vendor quotes", e);
            return ResponseEntity.badRequest().body("Failed to fetch quotes");
        }
    }

    @GetMapping("/my-received-quotes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getMyReceivedQuotes(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 HttpServletRequest request) {
        try {
            Long userId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body("User not authenticated");
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Quote> quotes = quoteService.getQuotesByUser(userId, pageable);
            return ResponseEntity.ok(quotes);

        } catch (Exception e) {
            log.error("Error fetching user quotes", e);
            return ResponseEntity.badRequest().body("Failed to fetch quotes");
        }
    }

    @PutMapping("/{quoteId}/accept")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> acceptQuote(@PathVariable Long quoteId) {
        try {
            Quote quote = quoteService.acceptQuote(quoteId);
            return ResponseEntity.ok(quote);
        } catch (Exception e) {
            log.error("Error accepting quote", e);
            return ResponseEntity.badRequest().body("Failed to accept quote");
        }
    }
}

