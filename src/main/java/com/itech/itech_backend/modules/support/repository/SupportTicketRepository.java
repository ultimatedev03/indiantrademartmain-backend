package com.itech.itech_backend.modules.support.repository;

import com.itech.itech_backend.modules.support.model.SupportTicket;
import com.itech.itech_backend.enums.TicketStatus;
import com.itech.itech_backend.enums.TicketPriority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    long countByStatus(String status);
    long countByStatus(TicketStatus status);
    
    // Enhanced methods
    Page<SupportTicket> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<SupportTicket> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<SupportTicket> findByStatusOrderByCreatedAtDesc(TicketStatus status, Pageable pageable);
    Page<SupportTicket> findByPriorityOrderByCreatedAtDesc(TicketPriority priority, Pageable pageable);
    
    Optional<SupportTicket> findByTicketNumber(String ticketNumber);
    
    // Priority and status count methods
    long countByPriority(TicketPriority priority);
    
    // Assignment methods
    List<SupportTicket> findByAssignedToIsNullAndStatusIn(List<TicketStatus> statuses);
    List<SupportTicket> findByAssignedToIdAndStatusIn(Long assignedToId, List<TicketStatus> statuses);
}

