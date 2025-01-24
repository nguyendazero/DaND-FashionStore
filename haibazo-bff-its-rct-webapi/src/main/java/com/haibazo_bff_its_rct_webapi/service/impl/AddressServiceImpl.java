package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddAddressRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctAddressResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctUserResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.exception.UnauthorizedException;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.AddressRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserRepository;
import com.haibazo_bff_its_rct_webapi.repository.WardRepository;
import com.haibazo_bff_its_rct_webapi.service.AddressService;
import com.haibazo_bff_its_rct_webapi.utils.TokenUtil;
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
    private final TokenUtil tokenUtil;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctAddressResponse> create(AddAddressRequest request, String authorizationHeader) {
        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;

        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (userResponse == null) {
            throw new UnauthorizedException();
        }

        User user = userRepository.findById(userResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userResponse.getId().toString()));

        Ward ward = wardRepository.findById(request.getWardId())
                .orElseThrow(() -> new ResourceNotFoundException("Ward", "id", request.getWardId().toString()));

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
    public APICustomize<ItsRctAddressResponse> update(Long id, AddAddressRequest request, String authorizationHeader) {
        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;

        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (userResponse == null) {
            throw new UnauthorizedException();
        }

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id.toString()));

        // Kiểm tra xem người dùng có quyền sửa địa chỉ không
        if (!address.getUser().getId().equals(userResponse.getId())) {
            throw new UnauthorizedException();
        }

        Ward ward = wardRepository.findById(request.getWardId())
                .orElseThrow(() -> new ResourceNotFoundException("Ward", "id", request.getWardId().toString()));

        address.setDisplayName(request.getDisplayName());
        address.setFirstName(request.getFirstName());
        address.setLastName(request.getLastName());
        address.setStreetAddress(request.getStreetAddress());
        address.setEmail(request.getEmail());
        address.setPhone(request.getPhone());
        address.setUpdatedAt(LocalDateTime.now());
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
    public APICustomize<String> delete(Long id, String authorizationHeader) {
        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;

        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (userResponse == null) {
            throw new UnauthorizedException();
        }

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id.toString()));

        // Kiểm tra xem người dùng có quyền xóa địa chỉ không
        if (!address.getUser().getId().equals(userResponse.getId())) {
            throw new UnauthorizedException();
        }

        addressRepository.delete(address);
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted address with id = " + id);
    }
}
