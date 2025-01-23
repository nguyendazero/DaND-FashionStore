package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddContactRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctContactResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctUserResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Contact;
import com.haibazo_bff_its_rct_webapi.model.Product;
import com.haibazo_bff_its_rct_webapi.model.User;
import com.haibazo_bff_its_rct_webapi.model.UserTemp;
import com.haibazo_bff_its_rct_webapi.repository.ContactRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserTempRepository;
import com.haibazo_bff_its_rct_webapi.service.ContactService;
import com.haibazo_bff_its_rct_webapi.utils.TokenUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final TokenUtil tokenUtil;
    private final ContactRepository contactRepository;
    private final UserTempRepository userTempRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctContactResponse> add(AddContactRequest request, String authorizationHeader) {
        Contact contact = new Contact();

        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = null;

        if (token != null) {
            // Giải mã token để lấy haibazoAccountId
            Long haibazoAccountId = tokenUtil.getHaibazoAccountIdFromToken(token);

            // Gọi account-service để lấy thông tin tài khoản
            userResponse = tokenUtil.getUserByHaibazoAccountId(haibazoAccountId);
        }

        // Nếu người dùng đã đăng nhập, sử dụng thông tin của họ
        if (userResponse != null) {
            contact.setFullName(userResponse.getFullName());
            contact.setEmail(userResponse.getEmail());
            contact.setUser(new User(userResponse.getId(), userResponse.getHaibazoAuthAlias()));
        } else {
            // Nếu chưa đăng nhập, kiểm tra thông tin từ request
            if (request.getFullName() != null) {
                contact.setFullName(request.getFullName());
            } else {
                // Xử lý trường hợp không có thông tin fullName
                // Ví dụ: có thể throw exception hoặc gán giá trị mặc định
                throw new IllegalArgumentException("Full name is required.");
            }

            if (request.getEmail() != null) {
                contact.setEmail(request.getEmail());
            } else {
                // Xử lý trường hợp không có thông tin email
                throw new IllegalArgumentException("Email is required.");
            }

            // Tạo UserTemp
            UserTemp userTemp = new UserTemp();
            userTemp.setFullName(contact.getFullName());
            userTemp.setEmail(contact.getEmail());
            userTemp.setAvatar(null);
            userTempRepository.save(userTemp);

            contact.setUserTemp(userTemp);
        }

        // Thiết lập thông điệp
        contact.setMessage(request.getMessage());

        // Lưu contact vào cơ sở dữ liệu
        Contact savedContact = contactRepository.save(contact);

        // Tạo phản hồi
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
