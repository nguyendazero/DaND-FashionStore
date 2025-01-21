package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddStateRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctStateResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceAlreadyExistsException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Country;
import com.haibazo_bff_its_rct_webapi.model.State;
import com.haibazo_bff_its_rct_webapi.repository.CountryRepository;
import com.haibazo_bff_its_rct_webapi.repository.StateRepository;
import com.haibazo_bff_its_rct_webapi.service.StateService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StateServiceImpl implements StateService {

    private final StateRepository stateRepository;
    private final CountryRepository countryRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctStateResponse> add(AddStateRequest request) {

        if (stateRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("State", "name", request.getName());
        }

        Country country = countryRepository.findByCode(request.getCountryCode())
                .orElseThrow(() -> new ResourceNotFoundException("Country", "code", request.getCountryCode()));

        State state = new State();
        state.setName(request.getName());
        state.setCountry(country);

        State savedState = stateRepository.save(state);

        ItsRctStateResponse response = new ItsRctStateResponse(
                savedState.getId(),
                savedState.getName(),
                savedState.getCountry().getCode(),
                savedState.getCreatedAt(),
                savedState.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctStateResponse>> states(String countryCode) {

        Country country = countryRepository.findByCode(countryCode)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "code", countryCode));

        List<State> states = stateRepository.findAllByCountryCode(countryCode);
        List<ItsRctStateResponse> response = states.stream()
                .map(state -> new ItsRctStateResponse(
                        state.getId(),
                        state.getName(),
                        state.getCountry().getCode(),
                        state.getCreatedAt(),
                        state.getUpdatedAt()
                )).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctStateResponse> state(Long id) {

        State state = stateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("State", "id", id.toString()));

        ItsRctStateResponse response = new ItsRctStateResponse(
                state.getId(),
                state.getName(),
                state.getCountry().getCode(),
                state.getCreatedAt(),
                state.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> delete(Long id) {

        State state = stateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("State", "id", id.toString()));

        stateRepository.delete(state);

        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted state with id = " + state.getId());
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctStateResponse> update(Long id, AddStateRequest request) {
        State state = stateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("State", "id", id.toString()));

        state.setName(request.getName());
        state.setUpdatedAt(LocalDateTime.now());
        State updatedState = stateRepository.save(state);

        ItsRctStateResponse response = new ItsRctStateResponse(
                updatedState.getId(),
                updatedState.getName(),
                updatedState.getCountry().getCode(),
                updatedState.getCreatedAt(),
                updatedState.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }
}
