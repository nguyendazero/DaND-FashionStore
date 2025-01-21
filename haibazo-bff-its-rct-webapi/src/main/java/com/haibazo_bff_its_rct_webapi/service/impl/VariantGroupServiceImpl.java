package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddVariantGroupRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctVariantGroupResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceAlreadyExistsException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.VariantGroup;
import com.haibazo_bff_its_rct_webapi.repository.VariantGroupRepository;
import com.haibazo_bff_its_rct_webapi.service.VariantGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VariantGroupServiceImpl implements VariantGroupService {

    private final VariantGroupRepository variantGroupRepository;

    @Override
    public APICustomize<List<ItsRctVariantGroupResponse>> variantGroups() {
        List<VariantGroup> variantGroups = variantGroupRepository.findAll();
        List<ItsRctVariantGroupResponse> variantGroupResponses = variantGroups.stream()
                .map(variantGroup -> new ItsRctVariantGroupResponse(
                        variantGroup.getVariantKey(),
                        variantGroup.getName(),
                        variantGroup.getCreatedAt(),
                        variantGroup.getUpdatedAt()
                )).toList();
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), variantGroupResponses);
    }

    @Override
    public APICustomize<ItsRctVariantGroupResponse> variantGroup(String key) {

        VariantGroup variantGroup = variantGroupRepository.findByVariantKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("VariantGroup", "key", key));

        ItsRctVariantGroupResponse variantGroupResponse = new ItsRctVariantGroupResponse(
                variantGroup.getVariantKey(),
                variantGroup.getName(),
                variantGroup.getCreatedAt(),
                variantGroup.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), variantGroupResponse);
    }

    @Override
    public APICustomize<ItsRctVariantGroupResponse> add(AddVariantGroupRequest request) {

        if (variantGroupRepository.existsByVariantKey(request.getKey())) {
            throw new ResourceAlreadyExistsException("VariantGroup", "key", request.getKey());
        }

        if (variantGroupRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("VariantGroup", "name", request.getName());
        }

        VariantGroup variantGroup = new VariantGroup();
        variantGroup.setVariantKey(request.getKey());
        variantGroup.setName(request.getName());

        VariantGroup savedVariantGroup = variantGroupRepository.save(variantGroup);

        ItsRctVariantGroupResponse variantGroupResponse = new ItsRctVariantGroupResponse(
                savedVariantGroup.getVariantKey(),
                savedVariantGroup.getName(),
                savedVariantGroup.getCreatedAt(),
                savedVariantGroup.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), variantGroupResponse);

    }

    @Override
    public APICustomize<String> delete(String key) {

        VariantGroup variantGroup = variantGroupRepository.findByVariantKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("VariantGroup", "key",key));

        variantGroupRepository.delete(variantGroup);
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted category with key = " + variantGroup.getVariantKey());
    }
}
