package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddTagRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctTagResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceAlreadyExistsException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Language;
import com.haibazo_bff_its_rct_webapi.model.Tag;
import com.haibazo_bff_its_rct_webapi.repository.TagRepository;
import com.haibazo_bff_its_rct_webapi.service.TagService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctTagResponse> add(AddTagRequest request) {
        // Kiểm tra xem tag với tên đã cho có tồn tại không
        if (tagRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Tag", "name", request.getName());
        }

        Tag tag = new Tag();
        tag.setName(request.getName());

        Tag savedTag = tagRepository.save(tag);

        ItsRctTagResponse tagResponse = new ItsRctTagResponse(
                savedTag.getId(),
                savedTag.getName(),
                savedTag.getCreatedAt(),
                savedTag.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), tagResponse);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctTagResponse>> tags() {
        List<Tag> tags = tagRepository.findAll();

        List<ItsRctTagResponse> tagResponses = tags.stream()
                .map(tag -> new ItsRctTagResponse(
                        tag.getId(),
                        tag.getName(),
                        tag.getCreatedAt(),
                        tag.getUpdatedAt()
                )).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), tagResponses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctTagResponse> tag(Long id) {

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id.toString()));

        ItsRctTagResponse tagResponse = new ItsRctTagResponse(
                tag.getId(),
                tag.getName(),
                tag.getCreatedAt(),
                tag.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), tagResponse);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> delete(Long id) {

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id.toString()));

        tagRepository.delete(tag);
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted tag with id = " + tag.getId());
    }
}
