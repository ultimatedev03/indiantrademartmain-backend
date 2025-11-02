package com.itech.itech_backend.modules.support.controller;

import com.itech.itech_backend.modules.shared.dto.ContactMessageDto;
import com.itech.itech_backend.modules.support.model.ContactMessage;
import com.itech.itech_backend.modules.support.service.ContactMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactMessageController {

    private final ContactMessageService contactService;

    @PostMapping
    public ContactMessage saveMessage(@RequestBody ContactMessageDto dto) {
        return contactService.saveMessage(dto);
    }
}

