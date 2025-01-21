package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCityRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCityResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceAlreadyExistsException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.City;
import com.haibazo_bff_its_rct_webapi.model.Country;
import com.haibazo_bff_its_rct_webapi.model.State;
import com.haibazo_bff_its_rct_webapi.repository.CityRepository;
import com.haibazo_bff_its_rct_webapi.repository.StateRepository;
import com.haibazo_bff_its_rct_webapi.service.CityService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final StateRepository stateRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCityResponse> create(AddCityRequest request) {

        if (cityRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("City", "name", request.getName());
        }

        State state = stateRepository.findById(request.getStateId())
                .orElseThrow(() -> new ResourceNotFoundException("State", "id", request.getStateId().toString()));

        City city = new City();
        city.setName(request.getName());
        city.setState(state);
        City savedCity = cityRepository.save(city);

        ItsRctCityResponse response = new ItsRctCityResponse(
                savedCity.getId(),
                savedCity.getName(),
                savedCity.getState().getId(),
                savedCity.getCreatedAt(),
                savedCity.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctCityResponse>> cities(Long stateId) {

        State state = stateRepository.findById(stateId)
                .orElseThrow(() -> new ResourceNotFoundException("State", "id", stateId.toString()));

        List<City> cities = cityRepository.findAllByStateId(stateId);
        List<ItsRctCityResponse> response = cities.stream()
                .map(city -> new ItsRctCityResponse(
                        city.getId(),
                        city.getName(),
                        city.getState().getId(),
                        city.getCreatedAt(),
                        city.getUpdatedAt()
                )).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCityResponse> city(Long id) {

        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", id.toString()));

        ItsRctCityResponse response = new ItsRctCityResponse(
                city.getId(),
                city.getName(),
                city.getState().getId(),
                city.getCreatedAt(),
                city.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCityResponse> update(Long id, AddCityRequest request) {

        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", id.toString()));

        if (cityRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("City", "name", request.getName());
        }

        city.setName(request.getName());
        city.setUpdatedAt(LocalDateTime.now());
        City savedCity = cityRepository.save(city);

        ItsRctCityResponse response = new ItsRctCityResponse(
                savedCity.getId(),
                savedCity.getName(),
                savedCity.getState().getId(),
                savedCity.getCreatedAt(),
                savedCity.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> delete(Long id) {

        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", id.toString()));

        cityRepository.delete(city);

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), "Successfully deleted city with id = " + city.getId());
    }
}
