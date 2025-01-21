package com.haibazo_bff_its_rct_webapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItsRctAddressResponse {
    private Long id;
    private String displayName;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String streetAddress;
    private Long wardId;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
