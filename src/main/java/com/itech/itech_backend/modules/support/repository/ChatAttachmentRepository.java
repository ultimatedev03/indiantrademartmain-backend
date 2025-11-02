package com.itech.itech_backend.modules.support.repository;

import com.itech.itech_backend.modules.support.model.ChatAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatAttachmentRepository extends JpaRepository<ChatAttachment, Long> {
    List<ChatAttachment> findByChatId(Long chatId);
}

