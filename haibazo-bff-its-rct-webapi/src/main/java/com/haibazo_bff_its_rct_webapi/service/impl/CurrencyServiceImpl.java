package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCurrencyRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCurrencyResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceAlreadyExistsException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Currency;
import com.haibazo_bff_its_rct_webapi.repository.CurrencyRepository;
import com.haibazo_bff_its_rct_webapi.service.CurrencyService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctCurrencyResponse>> currencies() {
        List<Currency> currencies = currencyRepository.findAll();
        List<ItsRctCurrencyResponse> currencyResponses = currencies.stream()
                .map(currency -> new ItsRctCurrencyResponse(
                        currency.getCode(),
                        currency.getName(),
                        currency.getRegion(),
                        currency.getCreatedAt(),
                        currency.getUpdatedAt()
                )).toList();
        return  new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), currencyResponses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCurrencyResponse> getCurrencyByCode(String code) {

        Currency currency = currencyRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "code", code));

        ItsRctCurrencyResponse currencyResponse = new ItsRctCurrencyResponse(
                currency.getCode(),
                currency.getName(),
                currency.getRegion(),
                currency.getCreatedAt(),
                currency.getUpdatedAt()
        );
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), currencyResponse);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCurrencyResponse> addCurrency(AddCurrencyRequest request) {

        if (currencyRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Currency", "name", request.getName());
        }

        Currency currency = new Currency();
        currency.setCode(request.getCode());
        currency.setName(request.getName());
        currency.setRegion(request.getRegion());

        Currency savedCurrency = currencyRepository.save(currency);

        ItsRctCurrencyResponse currencyResponse = new ItsRctCurrencyResponse(
                savedCurrency.getCode(),
                savedCurrency.getName(),
                savedCurrency.getRegion(),
                savedCurrency.getCreatedAt(),
                savedCurrency.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), currencyResponse);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> deleteCurrency(String code) {
        Currency currency = currencyRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "code", code));
        currencyRepository.delete(currency);
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted languages with code = " + currency.getCode());
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCurrencyResponse> updateCurrency(String code, AddCurrencyRequest request) {
        Currency currency = currencyRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "code", code));

        currency.setName(request.getName());
        currency.setRegion(request.getRegion());
        Currency updatedCurrency = currencyRepository.save(currency);

        ItsRctCurrencyResponse currencyResponse = new ItsRctCurrencyResponse(
                updatedCurrency.getCode(),
                updatedCurrency.getName(),
                updatedCurrency.getRegion(),
                updatedCurrency.getCreatedAt(),
                updatedCurrency.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), currencyResponse);
    }
}
