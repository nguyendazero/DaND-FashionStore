package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddOrderRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctOrderResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.enums.OrderStatus;
import com.haibazo_bff_its_rct_webapi.exception.*;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.*;
import com.haibazo_bff_its_rct_webapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final AddressRepository addressRepository;
    private final CouponRepository couponRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserCouponRepository userCouponRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public APICustomize<ItsRctOrderResponse> create(AddOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId().toString()));

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", request.getAddressId().toString()));

        // Kiểm tra coupon bằng couponCode
        Coupon coupon;
        if (request.getCouponCode() != null) {
            coupon = couponRepository.findByCode(request.getCouponCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Coupon", "code", request.getCouponCode()));

            // Kiểm tra nếu người dùng có coupon
            UserCoupon userCoupon = userCouponRepository.findByUserAndCoupon(user, coupon)
                    .orElseThrow(() -> new ResourceNotFoundException("UserCoupon", "userId and couponId", user.getId() + " and " + coupon.getId()));

            // Kiểm tra coupon đã được sử dụng chưa
            if (userCoupon.isUsed()) {
                throw new CouponAlreadyUsedException();
            }

            // Kiểm tra coupon đã hết hạn chưa
            if (LocalDateTime.now().isAfter(coupon.getEndDate())) {
                throw new CouponExpiredException();
            }
        } else {
            coupon = null;
        }

        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", request.getPaymentId().toString()));

        // Lấy danh sách CartItem và kiểm tra xem có rỗng không
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new CartEmptyException(user.getId());
        }

        // Tính tổng giá trị đơn hàng
        BigDecimal totalPrice = cartItems.stream()
                .map(cartItem -> {
                    BigDecimal price = cartItem.getProductAvailableVariant().getPrice();
                    long quantity = cartItem.getQuantity();
                    return price.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Kiểm tra nếu tổng giá trị đơn hàng lớn hơn minSpend của coupon
        if (coupon != null && totalPrice.compareTo(coupon.getMinSpend()) < 0) {
            throw new MinSpendCouponException(coupon.getMinSpend());
        }

        // Kiểm tra stock trước khi tạo đơn hàng
        for (CartItem cartItem : cartItems) {
            ProductAvailableVariant productAvailableVariant = cartItem.getProductAvailableVariant();
            long quantity = cartItem.getQuantity();

            // Kiểm tra số lượng trong kho
            if (productAvailableVariant.getStock() < quantity) {
                throw new InsufficientStockException(productAvailableVariant.getProduct().getName());
            }
        }

        // Tạo đơn hàng
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setCoupon(coupon);
        order.setPayment(payment);
        order.setTotalPrice(totalPrice);
        order.setOrderStatus(OrderStatus.PROCESSING);
        order.setNote(request.getNote());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Thêm OrderDetail cho từng sản phẩm trong giỏ hàng và cập nhật stock
        for (CartItem cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(savedOrder);
            orderDetail.setProductAvailableVariant(cartItem.getProductAvailableVariant());
            orderDetail.setQuantity(cartItem.getQuantity());

            // Cập nhật stock
            ProductAvailableVariant productAvailableVariant = cartItem.getProductAvailableVariant();
            long newStock = productAvailableVariant.getStock() - cartItem.getQuantity();
            productAvailableVariant.setStock(newStock);

            // Lưu OrderDetail
            orderDetailRepository.save(orderDetail);
        }

        // Xóa các CartItem đã được xử lý
        cartItemRepository.deleteAll(cartItems);

        // Đánh dấu coupon là đã sử dụng
        if (coupon != null) {
            UserCoupon userCoupon = userCouponRepository.findByUserAndCoupon(user, coupon).orElseThrow();
            userCoupon.setUsed(true);
            userCouponRepository.save(userCoupon);
        }

        // Tạo response
        ItsRctOrderResponse response = new ItsRctOrderResponse();
        response.setId(savedOrder.getId());
        response.setUserId(savedOrder.getUser().getId());
        response.setTotalPrice(savedOrder.getTotalPrice());
        response.setStatus(savedOrder.getOrderStatus());
        response.setNote(savedOrder.getNote());
        response.setCouponId(coupon != null ? coupon.getId() : null);
        response.setPaymentId(savedOrder.getPayment().getId());
        response.setAddressId(savedOrder.getAddress().getId());
        response.setCreatedAt(savedOrder.getCreatedAt());
        response.setUpdatedAt(savedOrder.getUpdatedAt());

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), response);
    }

    @Override
    public APICustomize<List<ItsRctOrderResponse>> getOrdersByUserId(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        List<Order> orders = orderRepository.findByUserId(user.getId());

        List<ItsRctOrderResponse> responses = orders.stream()
                .map(order -> {
                    ItsRctOrderResponse response = new ItsRctOrderResponse();
                    response.setId(order.getId());
                    response.setUserId(order.getUser().getId());
                    response.setTotalPrice(order.getTotalPrice());
                    response.setStatus(order.getOrderStatus());
                    response.setNote(order.getNote());
                    response.setCouponId(order.getCoupon() != null ? order.getCoupon().getId() : null);
                    response.setPaymentId(order.getPayment().getId());
                    response.setAddressId(order.getAddress().getId());
                    response.setCreatedAt(order.getCreatedAt());
                    response.setUpdatedAt(order.getUpdatedAt());
                    return response;
                })
                .toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), responses);

    }
}
