package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddOrderRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctOrderResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctUserResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.OrderStatisticsResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.enums.CouponType;
import com.haibazo_bff_its_rct_webapi.enums.OrderStatus;
import com.haibazo_bff_its_rct_webapi.exception.*;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.*;
import com.haibazo_bff_its_rct_webapi.service.OrderService;
import com.haibazo_bff_its_rct_webapi.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
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
    private final TokenUtil tokenUtil;

    @Override
    public APICustomize<ItsRctOrderResponse> create(AddOrderRequest request, String authorizationHeader) {
        // Lấy JWT từ header và kiểm tra người dùng
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;

        if (userResponse == null) {
            throw new UnauthorizedException();
        }

        User user = userRepository.findById(userResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userResponse.getId().toString()));

        // Kiểm tra địa chỉ có phải của người dùng không
        Address address = addressRepository.findById(request.getAddressId())
                .filter(addr -> addr.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("AddressUser", "addressId", request.getAddressId().toString()));

        Coupon coupon;
        if (request.getCouponCode() != null) {
            coupon = couponRepository.findByCode(request.getCouponCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Coupon", "code", request.getCouponCode()));

            UserCoupon userCoupon = userCouponRepository.findByUserAndCoupon(user, coupon)
                    .orElseThrow(() -> new ResourceNotFoundException("UserCoupon", "userId and couponId",
                            user.getId() + " and " + coupon.getId()));

            if (userCoupon.isUsed()) {
                throw new CouponAlreadyUsedException();
            }
            if (LocalDateTime.now().isAfter(coupon.getEndDate())) {
                throw new CouponExpiredException();
            }
        } else {
            coupon = null;
        }

        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", request.getPaymentId().toString()));

        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new CartEmptyException(user.getId());
        }

        BigDecimal totalPrice = cartItems.stream()
                .map(cartItem -> cartItem.getProductAvailableVariant().getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Kiểm tra coupon
        if (coupon != null) {
            if (totalPrice.compareTo(coupon.getMinSpend()) < 0) {
                throw new MinSpendCouponException(coupon.getMinSpend());
            }

            // Tính toán lại giá trị đơn hàng dựa trên discount của coupon
            totalPrice = totalPrice.multiply(BigDecimal.ONE.subtract(coupon.getDiscount().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)));
        }

        for (CartItem cartItem : cartItems) {
            ProductAvailableVariant productAvailableVariant = cartItem.getProductAvailableVariant();
            if (productAvailableVariant.getStock() < cartItem.getQuantity()) {
                throw new InsufficientStockException(productAvailableVariant.getProduct().getName());
            }
        }

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

        for (CartItem cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(savedOrder);
            orderDetail.setProductAvailableVariant(cartItem.getProductAvailableVariant());
            orderDetail.setQuantity(cartItem.getQuantity());

            ProductAvailableVariant variant = cartItem.getProductAvailableVariant();
            variant.setStock(variant.getStock() - cartItem.getQuantity());

            orderDetailRepository.save(orderDetail);
        }

        cartItemRepository.deleteAll(cartItems);

        if (coupon != null) {
            UserCoupon userCoupon = userCouponRepository.findByUserAndCoupon(user, coupon).orElseThrow();
            userCoupon.setUsed(true);
            userCouponRepository.save(userCoupon);
        }

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
    public APICustomize<List<ItsRctOrderResponse>> getOrdersByToken(String authorizationHeader) {
        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;

        assert userResponse != null;
        User user = userRepository.findById(userResponse.getId())
                .orElseThrow(ErrorPermissionException::new);

        List<Order> orders = orderRepository.findByUserId(user.getId());

        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("Order", "userId", user.getId().toString());
        }

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

    @Override
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        return orderRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public APICustomize<OrderStatisticsResponse> getOrderStatisticsByMonthAndYear(String authorizationHeader, Integer month, int year) {
        // Lấy JWT từ header và kiểm tra người dùng
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;

        if (userResponse == null || !userResponse.getRole().equals("ROLE_ADMIN")) {
            throw new ErrorPermissionException();
        }

        // Nếu month không được cung cấp, thống kê theo tất cả các tháng trong năm
        if (month == null) {
            LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(year + 1, 1, 1, 0, 0).minusSeconds(1);

            // Thống kê đơn hàng theo năm
            List<Order> orders = orderRepository.findByCreatedAtBetween(startDate, endDate);
            int totalOrders = orders.size();
            BigDecimal totalRevenue = orders.stream()
                    .map(Order::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            OrderStatisticsResponse statistics = new OrderStatisticsResponse(0, year, totalOrders, totalRevenue); // month = 0 để chỉ ra là thống kê theo năm
            return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), statistics);
        } else {
            // Xác định ngày bắt đầu và kết thúc cho tháng và năm đã cho
            LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(year, month,
                    Month.of(month).length(Year.isLeap(year)), 23, 59, 59); // Ngày cuối cùng của tháng

            // Thống kê đơn hàng theo tháng và năm
            List<Order> orders = orderRepository.findByCreatedAtBetween(startDate, endDate);
            int totalOrders = orders.size();
            BigDecimal totalRevenue = orders.stream()
                    .map(Order::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            OrderStatisticsResponse statistics = new OrderStatisticsResponse(month, year, totalOrders, totalRevenue);
            return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), statistics);
        }
    }
}
