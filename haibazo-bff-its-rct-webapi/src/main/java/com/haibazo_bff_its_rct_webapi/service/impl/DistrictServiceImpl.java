package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddDistrictRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctDistrictResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceAlreadyExistsException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.City;
import com.haibazo_bff_its_rct_webapi.model.District;
import com.haibazo_bff_its_rct_webapi.repository.CityRepository;
import com.haibazo_bff_its_rct_webapi.repository.DistrictRepository;
import com.haibazo_bff_its_rct_webapi.service.DistrictService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DistrictServiceImpl implements DistrictService {

    private final DistrictRepository districtRepository;
    private final CityRepository cityRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctDistrictResponse> create(AddDistrictRequest request) {

        if (districtRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("District", "name", request.getName());
        }

        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", request.getCityId().toString()));

        District district = new District();
        district.setName(request.getName());
        district.setCity(city);
        District savedDistrict = districtRepository.save(district);

        ItsRctDistrictResponse response = new ItsRctDistrictResponse(
                savedDistrict.getId(),
                savedDistrict.getName(),
                savedDistrict.getCity().getId(),
                savedDistrict.getCreatedAt(),
                savedDistrict.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctDistrictResponse> district(Long id) {

        District district = districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("District", "id", id.toString()));

        ItsRctDistrictResponse response = new ItsRctDistrictResponse(
                district.getId(),
                district.getName(),
                district.getCity().getId(),
                district.getCreatedAt(),
                district.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);

    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctDistrictResponse>> districts(Long cityId) {

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", cityId.toString()));

        List<District> districts = districtRepository.findAllByCityId(cityId);

        List<ItsRctDistrictResponse> responses = districts.stream()
                .map(district -> new ItsRctDistrictResponse(
                        district.getId(),
                        district.getName(),
                        district.getCity().getId(),
                        district.getCreatedAt(),
                        district.getUpdatedAt()
                )).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), responses);

    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctDistrictResponse> update(Long id, AddDistrictRequest request) {

        District district = districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("District", "id", id.toString()));

        if (districtRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("District", "name", request.getName());
        }

        district.setName(request.getName());
        district.setUpdatedAt(LocalDateTime.now());
        District savedDistrict = districtRepository.save(district);

        ItsRctDistrictResponse response = new ItsRctDistrictResponse(
                savedDistrict.getId(),
                savedDistrict.getName(),
                savedDistrict.getCity().getId(),
                savedDistrict.getCreatedAt(),
                savedDistrict.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> delete(Long id) {

        District district = districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("District", "id", id.toString()));

        districtRepository.delete(district);

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), "Successfully deleted district with id = " + district.getId());
    }
}
