package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddAddressRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctAddressResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.AddressRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserRepository;
import com.haibazo_bff_its_rct_webapi.repository.WardRepository;
import com.haibazo_bff_its_rct_webapi.service.AddressService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final WardRepository wardRepository;
    private final UserRepository userRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctAddressResponse> create(AddAddressRequest request) {

        Ward ward = wardRepository.findById(request.getWardId())
                .orElseThrow(() -> new ResourceNotFoundException("Ward", "id", request.getWardId().toString()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId().toString()));

        Address address = new Address();
        address.setDisplayName(request.getDisplayName());
        address.setFirstName(request.getFirstName());
        address.setLastName(request.getLastName());
        address.setStreetAddress(request.getStreetAddress());
        address.setEmail(request.getEmail());
        address.setPhone(request.getPhone());
        address.setUser(user);
        address.setWard(ward);

        Address savedAddress = addressRepository.save(address);

        ItsRctAddressResponse response = new ItsRctAddressResponse(
                savedAddress.getId(),
                savedAddress.getDisplayName(),
                savedAddress.getFirstName(),
                savedAddress.getLastName(),
                savedAddress.getEmail(),
                savedAddress.getPhone(),
                savedAddress.getStreetAddress(),
                savedAddress.getWard().getId(),
                savedAddress.getUser().getId(),
                savedAddress.getCreatedAt(),
                savedAddress.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctAddressResponse> update(Long id, AddAddressRequest request) {

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id.toString()));

        Ward ward = wardRepository.findById(request.getWardId())
                .orElseThrow(() -> new ResourceNotFoundException("Ward", "id", request.getWardId().toString()));

        address.setDisplayName(request.getDisplayName());
        address.setFirstName(request.getFirstName());
        address.setLastName(request.getLastName());
        address.setStreetAddress(request.getStreetAddress());
        address.setEmail(request.getEmail());
        address.setPhone(request.getPhone());
        address.setUpdatedAt(LocalDateTime.now());
        address.setUser(address.getUser());
        address.setWard(ward);

        Address savedAddress = addressRepository.save(address);

        ItsRctAddressResponse response = new ItsRctAddressResponse(
                savedAddress.getId(),
                savedAddress.getDisplayName(),
                savedAddress.getFirstName(),
                savedAddress.getLastName(),
                savedAddress.getEmail(),
                savedAddress.getPhone(),
                savedAddress.getStreetAddress(),
                savedAddress.getWard().getId(),
                savedAddress.getUser().getId(),
                savedAddress.getCreatedAt(),
                savedAddress.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);

    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctAddressResponse>> addresses(Long wardId) {

        Ward ward = wardRepository.findById(wardId)
                .orElseThrow(() -> new ResourceNotFoundException("Ward", "id", wardId.toString()));

        List<ItsRctAddressResponse> response = addressRepository.findAllByWardId(wardId)
                .stream()
                .map(address -> new ItsRctAddressResponse(
                        address.getId(),
                        address.getDisplayName(),
                        address.getFirstName(),
                        address.getLastName(),
                        address.getEmail(),
                        address.getPhone(),
                        address.getStreetAddress(),
                        address.getWard().getId(),
                        address.getUser().getId(),
                        address.getCreatedAt(),
                        address.getUpdatedAt()
                )).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);

    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctAddressResponse> address(Long id) {

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id.toString()));

        ItsRctAddressResponse response = new ItsRctAddressResponse(
                address.getId(),
                address.getDisplayName(),
                address.getFirstName(),
                address.getLastName(),
                address.getEmail(),
                address.getPhone(),
                address.getStreetAddress(),
                address.getWard().getId(),
                address.getUser().getId(),
                address.getCreatedAt(),
                address.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);

    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> delete(Long id) {

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id.toString()));

        addressRepository.delete(address);
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted address with id = " + id);
    }
}
