package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddDiscountRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctDiscountResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Discount;
import com.haibazo_bff_its_rct_webapi.repository.DiscountRepository;
import com.haibazo_bff_its_rct_webapi.service.DiscountService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctDiscountResponse> discount(Long id) {

        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", "id", id.toString()));

        ItsRctDiscountResponse discountResponse = new ItsRctDiscountResponse(
            discount.getId(), discount.getDiscountValue(), discount.getDateEndSale()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), discountResponse);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctDiscountResponse>> discounts() {
        List<Discount> discounts = discountRepository.findAll();
        List<ItsRctDiscountResponse> responses = discounts.stream()
                .map(discount -> new ItsRctDiscountResponse(
                        discount.getId(), discount.getDiscountValue(), discount.getDateEndSale()
                )).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), responses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctDiscountResponse> add(AddDiscountRequest request) {
        Discount discount = new Discount();
        discount.setDiscountValue(request.getValue());
        discount.setDateEndSale(request.getDateEndSale());
        Discount savedDiscount = discountRepository.save(discount);

        ItsRctDiscountResponse discountResponse = new ItsRctDiscountResponse(
                savedDiscount.getId(), savedDiscount.getDiscountValue(), savedDiscount.getDateEndSale()
        );

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), discountResponse);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> delete(Long id) {

        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", "id", id.toString()));

        discountRepository.delete(discount);

        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted discount with id = " + id);
    }
}
