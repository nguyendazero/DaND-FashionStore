package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCountryRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCountryResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceAlreadyExistsException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Country;
import com.haibazo_bff_its_rct_webapi.repository.CountryRepository;
import com.haibazo_bff_its_rct_webapi.service.CountryService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctCountryResponse>> countries() {
        List<Country> countries = countryRepository.findAll();
        List<ItsRctCountryResponse> countryResponses = countries.stream()
                .map(country -> new ItsRctCountryResponse(
                        country.getCode(),
                        country.getName(),
                        country.getCreatedAt(),
                        country.getUpdatedAt()
                )).toList();
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), countryResponses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCountryResponse> countryByCode(String code) {

        Country country = countryRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "code", code));

        ItsRctCountryResponse countryResponse =  new ItsRctCountryResponse(
                country.getCode(),
                country.getName(),
                country.getCreatedAt(),
                country.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), countryResponse);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCountryResponse> addCountry(AddCountryRequest request) {

        if (countryRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Country", "name", request.getName());
        }

        Country newCountry = new Country();
        newCountry.setCode(request.getCode());
        newCountry.setName(request.getName());

        Country savedCountry = countryRepository.save(newCountry);

        ItsRctCountryResponse countryResponse = new ItsRctCountryResponse(
                savedCountry.getCode(), savedCountry.getName(),
                savedCountry.getCreatedAt(), savedCountry.getUpdatedAt()
        );
        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), countryResponse);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> deleteCountry(String code) {
        Country country = countryRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "code", code));
        countryRepository.delete(country);
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted country with code = " + country.getCode());
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCountryResponse> updateCountry(String code, AddCountryRequest request) {
        Country country = countryRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "code", code));

        country.setName(request.getName());
        country.setUpdatedAt(LocalDateTime.now());
        countryRepository.save(country);

        ItsRctCountryResponse countryResponse =  new ItsRctCountryResponse(
                country.getCode(),
                country.getName(),
                country.getCreatedAt(),
                country.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), countryResponse);
    }
}
