package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddContactRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctContactResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Contact;
import com.haibazo_bff_its_rct_webapi.model.Product;
import com.haibazo_bff_its_rct_webapi.model.UserTemp;
import com.haibazo_bff_its_rct_webapi.repository.ContactRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserTempRepository;
import com.haibazo_bff_its_rct_webapi.service.ContactService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final UserTempRepository userTempRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctContactResponse> add(AddContactRequest request) {
        Contact contact = new Contact();
        contact.setFullName(request.getFullName());
        contact.setEmail(request.getEmail());
        contact.setMessage(request.getMessage());
        contact.setUser(null);

        UserTemp userTemp = new UserTemp();
        userTemp.setFullName(request.getFullName());
        userTemp.setEmail(request.getEmail());
        userTemp.setAvatar(null);
        userTempRepository.save(userTemp);

        contact.setUserTemp(userTemp);

        Contact savedContact = contactRepository.save(contact);

        ItsRctContactResponse response = new ItsRctContactResponse(
                savedContact.getId(),
                savedContact.getFullName(),
                savedContact.getEmail(),
                savedContact.getMessage(),
                savedContact.getCreatedAt(),
                savedContact.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctContactResponse>> contacts() {
        List<Contact> contacts = contactRepository.findAll();
        List<ItsRctContactResponse> responses = contacts.stream()
                .map(contact -> new ItsRctContactResponse(
                        contact.getId(),
                        contact.getFullName(),
                        contact.getEmail(),
                        contact.getMessage(),
                        contact.getCreatedAt(),
                        contact.getUpdatedAt()
                )).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), responses);

    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctContactResponse> contact(Long id) {

        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id.toString()));

        ItsRctContactResponse response = new ItsRctContactResponse(
                contact.getId(),
                contact.getFullName(),
                contact.getEmail(),
                contact.getMessage(),
                contact.getCreatedAt(),
                contact.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);

    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> delete(Long id) {

        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id.toString()));

        contactRepository.delete(contact);
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted contact with id = " + contact.getId());
    }
}
