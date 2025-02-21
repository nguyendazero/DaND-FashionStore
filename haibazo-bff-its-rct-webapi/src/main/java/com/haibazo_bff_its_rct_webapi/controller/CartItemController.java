package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.RemoveFromCartRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCartResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctUserResponse;
import com.haibazo_bff_its_rct_webapi.exception.*;
import com.haibazo_bff_its_rct_webapi.model.CartItem;
import com.haibazo_bff_its_rct_webapi.model.Coupon;
import com.haibazo_bff_its_rct_webapi.model.User;
import com.haibazo_bff_its_rct_webapi.model.UserCoupon;
import com.haibazo_bff_its_rct_webapi.repository.CartItemRepository;
import com.haibazo_bff_its_rct_webapi.repository.CouponRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserCouponRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserRepository;
import com.haibazo_bff_its_rct_webapi.service.CartItemService;
import com.haibazo_bff_its_rct_webapi.utils.CookieUtil;
import com.haibazo_bff_its_rct_webapi.utils.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class CartItemController {

    private final CartItemService cartItemService;
    private final TokenUtil tokenUtil;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    @GetMapping("/public/cart-item/cart-items")
    public String cartItems(Model model, HttpServletRequest request){
        
        String jwtToken = CookieUtil.getJwtTokenFromCookies(request);
        if (jwtToken == null) throw new UnauthorizedException();
        model.addAttribute("jwtToken", jwtToken);
        
        APICustomize<List<ItsRctCartResponse>> response = cartItemService.getCartItems(jwtToken);
        BigDecimal totalPrice = cartItemService.calculateTotalPrice(jwtToken);
        BigDecimal discountAmount = (BigDecimal) model.getAttribute("discountAmount");
        model.addAttribute("discountAmount", discountAmount);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("items", response.getResult());
        return "cart";
    }


    @PostMapping("/public/cart-item/{variantId}")
    public RedirectView addToCart(Model model, @PathVariable Long variantId, HttpServletRequest request) {
        
        // Lấy cookie từ request
        String jwtToken = CookieUtil.getJwtTokenFromCookies(request);
        if (jwtToken == null) throw new UnauthorizedException();
        model.addAttribute("jwtToken", jwtToken);
        
        cartItemService.addToCart(variantId, "Bearer " + jwtToken);
        
        return new RedirectView("http://localhost:8386/api/bff/its-rct/v1/ecommerce/public/home");
    }

    @GetMapping("/public/cart-item/{variantId}")
    public RedirectView removeFromCart(@PathVariable Long variantId, HttpServletRequest httpRequest) {
        // Lấy cookie từ request
        String jwtToken = CookieUtil.getJwtTokenFromCookies(httpRequest);
        if (jwtToken == null) throw new UnauthorizedException();

        // Gọi service để xóa sản phẩm khỏi giỏ hàng
        APICustomize<String> response = cartItemService.removeFromCart(variantId, "Bearer " + jwtToken);

        // Trả về phản hồi
        return new RedirectView("http://localhost:8386/api/bff/its-rct/v1/ecommerce/public/home");
    }

    @PostMapping("/public/cart-item/plus/{variantId}")
    public ResponseEntity<?> increaseQuantity(@PathVariable Long variantId, HttpServletRequest httpRequest) {
        String jwtToken = CookieUtil.getJwtTokenFromCookies(httpRequest);
        if (jwtToken == null) throw new UnauthorizedException();

        APICustomize<String> response = cartItemService.changeQuantity(variantId, "Bearer " + jwtToken, 1);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/public/cart-item/minus/{variantId}")
    public ResponseEntity<?> decreaseQuantity(@PathVariable Long variantId, HttpServletRequest httpRequest) {
        String jwtToken = CookieUtil.getJwtTokenFromCookies(httpRequest);
        if (jwtToken == null) throw new UnauthorizedException();

        APICustomize<String> response = cartItemService.changeQuantity(variantId, "Bearer " + jwtToken, -1);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/public/cart-item/apply-coupon")
    public String applyCoupon(@RequestParam String code, HttpServletRequest request, Model model) {
        String jwtToken = CookieUtil.getJwtTokenFromCookies(request);
        if (jwtToken == null) throw new UnauthorizedException();

        // Lấy thông tin người dùng từ token
        ItsRctUserResponse userResponse = tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(jwtToken));

        User user = userRepository.findById(userResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userResponse.getId().toString()));

        // Tìm coupon theo mã
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "code", code));

        // Kiểm tra xem người dùng đã sử dụng coupon này chưa
        UserCoupon userCoupon = userCouponRepository.findByUserAndCoupon(user, coupon)
                .orElse(null);

        if (userCoupon != null && userCoupon.isUsed()) {
            throw new CouponAlreadyUsedException();
        }

        // Kiểm tra thời gian hiệu lực của coupon
        if (LocalDateTime.now().isAfter(coupon.getEndDate())) {
            throw new CouponExpiredException();
        }

        // Lấy danh sách hàng trong giỏ
        List<CartItem> cartItems = cartItemRepository.findByUserId(userResponse.getId());
        if (cartItems.isEmpty()) {
            throw new CartEmptyException(userResponse.getId());
        }

        // Tính toán tổng giá trị đơn hàng
        BigDecimal totalPrice = cartItems.stream()
                .map(cartItem -> cartItem.getProductAvailableVariant().getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Kiểm tra giá trị tối thiểu cần chi tiêu
        if (totalPrice.compareTo(coupon.getMinSpend()) < 0) {
            throw new MinSpendCouponException(coupon.getMinSpend());
        }

        // Tính toán giá trị đơn hàng sau khi áp dụng coupon
        BigDecimal discountAmount = totalPrice.multiply(coupon.getDiscount().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP));
        totalPrice = totalPrice.subtract(discountAmount);

        // Cập nhật lại tổng giá trị cho mô hình
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("items", cartItems);
        model.addAttribute("discountAmount", discountAmount);

        // Gọi phương thức cartItems để trả về trang giỏ hàng
        return cartItems(model, request);
    }

}
