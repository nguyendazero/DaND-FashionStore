package com.haibazo_bff_its_rct_webapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddAddressRequest {
    private String displayName;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String streetAddress;
    private Long wardId;
}
