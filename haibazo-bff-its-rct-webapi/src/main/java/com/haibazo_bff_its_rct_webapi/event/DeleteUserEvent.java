package com.haibazo_bff_its_rct_webapi.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DeleteUserEvent {
    private Long haibazoAccountId;
}
