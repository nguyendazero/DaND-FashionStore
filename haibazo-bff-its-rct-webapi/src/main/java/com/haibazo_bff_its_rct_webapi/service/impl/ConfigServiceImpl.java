package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddConfigRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctConfigResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Config;
import com.haibazo_bff_its_rct_webapi.repository.ConfigRepository;
import com.haibazo_bff_its_rct_webapi.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigRepository configRepository;

    @Override
    public APICustomize<ItsRctConfigResponse> add(AddConfigRequest request) {
        Config config = new Config();
        config.setKey(request.getConfigKey());
        config.setValue(request.getValue());

        Config savedConfig = configRepository.save(config);

        ItsRctConfigResponse response = new ItsRctConfigResponse(
                savedConfig.getId(),
                savedConfig.getKey(),
                savedConfig.getValue(),
                savedConfig.getCreatedAt(),
                savedConfig.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);

    }

    @Override
    public APICustomize<ItsRctConfigResponse> update(Long id, AddConfigRequest request) {

        Config config = configRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Config", "id", id.toString()));

        config.setKey(request.getConfigKey());
        config.setValue(request.getValue());

        Config savedConfig = configRepository.save(config);

        ItsRctConfigResponse response = new ItsRctConfigResponse(
                savedConfig.getId(),
                savedConfig.getKey(),
                savedConfig.getValue(),
                savedConfig.getCreatedAt(),
                savedConfig.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);

    }

    @Override
    public APICustomize<ItsRctConfigResponse> config(Long id) {

        Config config = configRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Config", "id", id.toString()));

        ItsRctConfigResponse response = new ItsRctConfigResponse(
                config.getId(),
                config.getKey(),
                config.getValue(),
                config.getCreatedAt(),
                config.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    public APICustomize<List<ItsRctConfigResponse>> configs() {
        List<Config> configs = configRepository.findAll();
        List<ItsRctConfigResponse> response = configs.stream()
                .map(config -> new ItsRctConfigResponse(
                        config.getId(),
                        config.getKey(),
                        config.getValue(),
                        config.getCreatedAt(),
                        config.getUpdatedAt()
                )).toList();
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    public APICustomize<String> delete(Long id) {

        Config config = configRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Config", "id", id.toString()));

        configRepository.delete(config);
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted config with id = " + config.getId());
    }
}
