package com.itech.itech_backend.modules.support.service;

import com.itech.itech_backend.modules.shared.dto.ContactMessageDto;
import com.itech.itech_backend.modules.support.model.ContactMessage;
import com.itech.itech_backend.modules.support.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactMessageService {

    private final ContactMessageRepository contactRepo;

    public ContactMessage saveMessage(ContactMessageDto dto) {
        return contactRepo.save(ContactMessage.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .message(dto.getMessage())
                .build());
    }
}

