package com.itech.itech_backend.modules.directory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/directory")
public class DirectoryController {

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchServiceProviders(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "relevance") String sortBy) {
        
        try {
            // Mock providers data
            List<Map<String, Object>> mockProviders = createMockProviders();
            
            // Filter based on query and location
            List<Map<String, Object>> filteredProviders = filterProviders(mockProviders, query, location, category);
            
            // Pagination
            int startIndex = (page - 1) * limit;
            int endIndex = Math.min(startIndex + limit, filteredProviders.size());
            List<Map<String, Object>> paginatedProviders = new ArrayList<>();
            if (startIndex < filteredProviders.size()) {
                paginatedProviders = filteredProviders.subList(startIndex, endIndex);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("providers", paginatedProviders);
            response.put("total", filteredProviders.size());
            response.put("page", page);
            response.put("totalPages", (int) Math.ceil((double) filteredProviders.size() / limit));
            response.put("hasMore", endIndex < filteredProviders.size());
            
            Map<String, Object> filters = new HashMap<>();
            filters.put("query", query);
            filters.put("location", location);
            filters.put("category", category);
            filters.put("sortBy", sortBy);
            response.put("filters", filters);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("providers", Collections.emptyList());
            errorResponse.put("total", 0);
            errorResponse.put("page", page);
            errorResponse.put("totalPages", 0);
            errorResponse.put("hasMore", false);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/contact")
    public ResponseEntity<Map<String, Object>> contactServiceProvider(
            @RequestBody Map<String, Object> request) {
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Your inquiry has been sent successfully!");
            response.put("inquiryId", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to send inquiry. Please try again.");
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/providers/{id}")
    public ResponseEntity<Map<String, Object>> getServiceProvider(@PathVariable Long id) {
        try {
            Map<String, Object> provider = createMockProviderDetail(id);
            return ResponseEntity.ok(provider);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Helper methods for mock data
    private List<Map<String, Object>> createMockProviders() {
        List<Map<String, Object>> providers = new ArrayList<>();
        
        Map<String, Object> provider1 = new HashMap<>();
        provider1.put("id", 1L);
        provider1.put("businessName", "Advanced Land Surveyors");
        provider1.put("ownerName", "Rajesh Kumar");
        provider1.put("category", "Land Surveyors");
        provider1.put("rating", 4.8);
        provider1.put("reviewCount", 45);
        provider1.put("yearsOfExperience", 12);
        provider1.put("completedProjects", 250);
        provider1.put("responseTime", "2 hours");
        provider1.put("verified", true);
        
        Map<String, Object> location1 = new HashMap<>();
        location1.put("address", "Sector 62, Noida");
        location1.put("city", "Noida");
        location1.put("state", "Uttar Pradesh");
        location1.put("pincode", "201301");
        location1.put("area", "Sector 62");
        provider1.put("location", location1);
        
        Map<String, Object> contact1 = new HashMap<>();
        contact1.put("mobile", "+91 9876543210");
        contact1.put("phone", "0120-4567890");
        contact1.put("email", "contact@advancedsurveyors.com");
        contact1.put("website", "https://advancedsurveyors.com");
        provider1.put("contact", contact1);
        
        provider1.put("services", Arrays.asList("Topographical Survey", "Boundary Survey", "Construction Survey", "GPS Survey"));
        provider1.put("description", "Professional land surveying services with latest GPS technology and certified surveyors.");
        
        providers.add(provider1);
        
        Map<String, Object> provider2 = new HashMap<>();
        provider2.put("id", 2L);
        provider2.put("businessName", "Metro Construction Services");
        provider2.put("ownerName", "Amit Singh");
        provider2.put("category", "Construction Services");
        provider2.put("rating", 4.6);
        provider2.put("reviewCount", 67);
        provider2.put("yearsOfExperience", 8);
        provider2.put("completedProjects", 180);
        provider2.put("responseTime", "4 hours");
        provider2.put("verified", true);
        
        Map<String, Object> location2 = new HashMap<>();
        location2.put("address", "Sec 18, Noida");
        location2.put("city", "Noida");
        location2.put("state", "Uttar Pradesh");
        location2.put("pincode", "201301");
        location2.put("area", "Sector 18");
        provider2.put("location", location2);
        
        Map<String, Object> contact2 = new HashMap<>();
        contact2.put("mobile", "+91 9123456789");
        contact2.put("phone", "0120-9876543");
        contact2.put("email", "info@metroconstruction.com");
        provider2.put("contact", contact2);
        
        provider2.put("services", Arrays.asList("Building Construction", "Interior Work", "Renovation", "Commercial Construction"));
        provider2.put("description", "Complete construction solutions from residential to commercial projects.");
        
        providers.add(provider2);
        
        Map<String, Object> provider3 = new HashMap<>();
        provider3.put("id", 3L);
        provider3.put("businessName", "Delhi Legal Associates");
        provider3.put("ownerName", "Priya Sharma");
        provider3.put("category", "Legal Services");
        provider3.put("rating", 4.9);
        provider3.put("reviewCount", 89);
        provider3.put("yearsOfExperience", 15);
        provider3.put("completedProjects", 500);
        provider3.put("responseTime", "1 hour");
        provider3.put("verified", true);
        
        Map<String, Object> location3 = new HashMap<>();
        location3.put("address", "Connaught Place");
        location3.put("city", "New Delhi");
        location3.put("state", "Delhi");
        location3.put("pincode", "110001");
        location3.put("area", "Connaught Place");
        provider3.put("location", location3);
        
        Map<String, Object> contact3 = new HashMap<>();
        contact3.put("mobile", "+91 8765432109");
        contact3.put("phone", "011-23456789");
        contact3.put("email", "contact@delhilegal.com");
        provider3.put("contact", contact3);
        
        provider3.put("services", Arrays.asList("Property Law", "Business Law", "Civil Litigation", "Corporate Law"));
        provider3.put("description", "Expert legal services with 15 years of experience in property and corporate law.");
        
        providers.add(provider3);
        
        // Add more providers for different categories and locations
        Map<String, Object> provider4 = new HashMap<>();
        provider4.put("id", 4L);
        provider4.put("businessName", "Elite CA Services");
        provider4.put("ownerName", "Suresh Gupta");
        provider4.put("category", "CA Services");
        provider4.put("rating", 4.7);
        provider4.put("reviewCount", 92);
        provider4.put("yearsOfExperience", 18);
        provider4.put("completedProjects", 400);
        provider4.put("responseTime", "3 hours");
        provider4.put("verified", true);
        
        Map<String, Object> location4 = new HashMap<>();
        location4.put("address", "FC Road");
        location4.put("city", "Pune");
        location4.put("state", "Maharashtra");
        location4.put("pincode", "411005");
        location4.put("area", "FC Road");
        provider4.put("location", location4);
        
        Map<String, Object> contact4 = new HashMap<>();
        contact4.put("mobile", "+91 9988776655");
        contact4.put("phone", "020-25612345");
        contact4.put("email", "info@eliteca.com");
        provider4.put("contact", contact4);
        
        provider4.put("services", Arrays.asList("Tax Filing", "Audit Services", "Business Registration", "GST Services"));
        provider4.put("description", "Comprehensive CA services with expertise in taxation and business compliance.");
        
        providers.add(provider4);
        
        Map<String, Object> provider5 = new HashMap<>();
        provider5.put("id", 5L);
        provider5.put("businessName", "TechFlow IT Solutions");
        provider5.put("ownerName", "Rahul Patel");
        provider5.put("category", "IT Services");
        provider5.put("rating", 4.5);
        provider5.put("reviewCount", 78);
        provider5.put("yearsOfExperience", 6);
        provider5.put("completedProjects", 150);
        provider5.put("responseTime", "2 hours");
        provider5.put("verified", true);
        
        Map<String, Object> location5 = new HashMap<>();
        location5.put("address", "Electronic City");
        location5.put("city", "Bangalore");
        location5.put("state", "Karnataka");
        location5.put("pincode", "560100");
        location5.put("area", "Electronic City");
        provider5.put("location", location5);
        
        Map<String, Object> contact5 = new HashMap<>();
        contact5.put("mobile", "+91 9876012345");
        contact5.put("phone", "080-41234567");
        contact5.put("email", "contact@techflow.com");
        provider5.put("contact", contact5);
        
        provider5.put("services", Arrays.asList("Web Development", "Mobile Apps", "Software Development", "Cloud Services"));
        provider5.put("description", "Leading IT services company specializing in web and mobile application development.");
        
        providers.add(provider5);
        
        return providers;
    }
    
    private List<Map<String, Object>> filterProviders(List<Map<String, Object>> providers, 
                                                    String query, String location, String category) {
        return providers.stream()
            .filter(provider -> {
                if (query != null && !query.isEmpty()) {
                    String businessName = (String) provider.get("businessName");
                    String cat = (String) provider.get("category");
                    String description = (String) provider.get("description");
                    @SuppressWarnings("unchecked")
                    List<String> services = (List<String>) provider.get("services");
                    
                    return (businessName != null && businessName.toLowerCase().contains(query.toLowerCase())) ||
                           (cat != null && cat.toLowerCase().contains(query.toLowerCase())) ||
                           (description != null && description.toLowerCase().contains(query.toLowerCase())) ||
                           (services != null && services.stream().anyMatch(s -> s.toLowerCase().contains(query.toLowerCase())));
                }
                return true;
            })
            .filter(provider -> {
                if (location != null && !location.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> loc = (Map<String, Object>) provider.get("location");
                    if (loc != null) {
                        String city = (String) loc.get("city");
                        String state = (String) loc.get("state");
                        String area = (String) loc.get("area");
                        
                        return (city != null && city.toLowerCase().contains(location.toLowerCase())) ||
                               (state != null && state.toLowerCase().contains(location.toLowerCase())) ||
                               (area != null && area.toLowerCase().contains(location.toLowerCase()));
                    }
                }
                return true;
            })
            .filter(provider -> {
                if (category != null && !category.isEmpty()) {
                    String cat = (String) provider.get("category");
                    return cat != null && cat.toLowerCase().contains(category.toLowerCase());
                }
                return true;
            })
            .collect(Collectors.toList());
    }
    
    private Map<String, Object> createMockProviderDetail(Long id) {
        List<Map<String, Object>> providers = createMockProviders();
        return providers.stream()
            .filter(p -> Objects.equals(p.get("id"), id))
            .findFirst()
            .orElse(providers.get(0)); // Return first provider if id not found
    }
}
