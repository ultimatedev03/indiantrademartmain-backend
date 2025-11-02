package com.itech.itech_backend.modules.support.repository;

import com.itech.itech_backend.modules.support.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
}

