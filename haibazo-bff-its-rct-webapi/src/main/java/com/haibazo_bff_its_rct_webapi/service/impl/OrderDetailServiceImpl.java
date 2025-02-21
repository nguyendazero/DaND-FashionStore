package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctOrderDetailResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductAvailableVariantResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctUserResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ErrorPermissionException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.exception.UnauthorizedException;
import com.haibazo_bff_its_rct_webapi.model.Order;
import com.haibazo_bff_its_rct_webapi.model.OrderDetail;
import com.haibazo_bff_its_rct_webapi.model.ProductAvailableVariant;
import com.haibazo_bff_its_rct_webapi.model.User;
import com.haibazo_bff_its_rct_webapi.repository.OrderDetailRepository;
import com.haibazo_bff_its_rct_webapi.repository.OrderRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserRepository;
import com.haibazo_bff_its_rct_webapi.service.OrderDetailService;
import com.haibazo_bff_its_rct_webapi.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final TokenUtil tokenUtil;
    private final UserRepository userRepository;

    @Override
    public APICustomize<List<ItsRctOrderDetailResponse>> orderDetails(Long orderId, String authorizationHeader) {
        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;

        assert userResponse != null;
        User user = userRepository.findById(userResponse.getId())
                .orElseThrow(UnauthorizedException::new);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId.toString()));

        // Kiểm tra xem đơn hàng có phải của người dùng không
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ErrorPermissionException();
        }

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);

        List<ItsRctOrderDetailResponse> responses = orderDetails.stream()
                .map(orderDetail -> {
                    ProductAvailableVariant productAvailableVariant = orderDetail.getProductAvailableVariant();

                    // Tạo đối tượng ItsRctProductAvailableVariantResponse
                    ItsRctProductAvailableVariantResponse productVariantResponse = new ItsRctProductAvailableVariantResponse();
                    productVariantResponse.setId(productAvailableVariant.getId());
                    productVariantResponse.setHighLightedImageUrl(productAvailableVariant.getHighLightedImageUrl());
                    productVariantResponse.setPrice(productAvailableVariant.getPrice());
                    productVariantResponse.setStock(productAvailableVariant.getStock());
                    productVariantResponse.setProductId(productAvailableVariant.getProduct().getId());

                    return new ItsRctOrderDetailResponse(
                            orderDetail.getId(),
                            orderDetail.getOrder().getId(),
                            productVariantResponse,
                            orderDetail.getQuantity(),
                            orderDetail.getCreatedAt()
                    );
                }).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), responses);
    }
}
