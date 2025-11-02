package com.itech.itech_backend.modules.shared.service;

import com.itech.itech_backend.modules.support.service.SLAConfigurationService;
import com.itech.marketplace.dto.SLAConfigurationDto;
import com.itech.marketplace.entity.SLAConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SLAConfigurationServiceImpl implements SLAConfigurationService {

    @Override
    public List<SLAConfiguration> getAllConfigurations() {
        return new ArrayList<>();
    }

    @Override
    public SLAConfiguration createConfiguration(SLAConfigurationDto dto) {
        SLAConfiguration config = new SLAConfiguration();
        return config;
    }

    @Override
    public SLAConfiguration updateConfiguration(Long id, SLAConfigurationDto dto) {
        SLAConfiguration config = new SLAConfiguration();
        config.setId(id);
        return config;
    }
}

