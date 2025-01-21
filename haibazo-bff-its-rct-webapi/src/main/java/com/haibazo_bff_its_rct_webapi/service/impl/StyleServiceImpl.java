package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddStyleRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctStyleResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceAlreadyExistsException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Style;
import com.haibazo_bff_its_rct_webapi.repository.StyleRepository;
import com.haibazo_bff_its_rct_webapi.service.StyleService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StyleServiceImpl implements StyleService {

    private final StyleRepository styleRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctStyleResponse>> styles() {
        List<Style> styles = styleRepository.findAll();
        List<ItsRctStyleResponse> styleResponses = styles.stream()
                .map(style -> new ItsRctStyleResponse(
                        style.getId(),
                        style.getName(),
                        style.getCreatedAt(),
                        style.getUpdatedAt()
                )).toList();
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), styleResponses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctStyleResponse> style(Long id) {
        Style style = styleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Style", "id", id.toString()));

        ItsRctStyleResponse styleResponse = new ItsRctStyleResponse(
                style.getId(),
                style.getName(),
                style.getCreatedAt(),
                style.getUpdatedAt()
        );
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), styleResponse);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctStyleResponse> add(AddStyleRequest request) {

        if (styleRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Style", "name", request.getName());
        }

        Style style = new Style();
        style.setName(request.getName());
        Style savedStyle = styleRepository.save(style);

        ItsRctStyleResponse styleResponse = new ItsRctStyleResponse(
                savedStyle.getId(),
                savedStyle.getName(),
                savedStyle.getCreatedAt(),
                savedStyle.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), styleResponse);

    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> delete(Long id) {
        Style style = styleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Style", "id", id.toString()));

        styleRepository.delete(style);
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted style with id = " + style.getId());
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctStyleResponse> update(Long id, AddStyleRequest request) {
        Style style = styleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Style", "id", id.toString()));

        style.setName(request.getName());
        Style updatedStyle = styleRepository.save(style);

        ItsRctStyleResponse styleResponse = new ItsRctStyleResponse(
                updatedStyle.getId(),
                updatedStyle.getName(),
                updatedStyle.getCreatedAt(),
                updatedStyle.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), styleResponse);


    }
}
