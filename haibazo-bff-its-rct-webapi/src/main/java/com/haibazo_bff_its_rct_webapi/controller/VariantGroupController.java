package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddVariantGroupRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctVariantGroupResponse;
import com.haibazo_bff_its_rct_webapi.service.VariantGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class VariantGroupController {

    private final VariantGroupService variantGroupService;

    @GetMapping("/user/variant-group/variant-groups")
    public ResponseEntity<?> variantGroups() {
        APICustomize<List<ItsRctVariantGroupResponse>> response = variantGroupService.variantGroups();
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/variant-group")
    public ResponseEntity<?> create(@RequestBody AddVariantGroupRequest request) {
        APICustomize<ItsRctVariantGroupResponse> response = variantGroupService.add(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/user/variant-group")
    public ResponseEntity<?> variantGroup(@RequestParam String key) {
        APICustomize<ItsRctVariantGroupResponse> response = variantGroupService.variantGroup(key);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/variant-group")
    public ResponseEntity<?> delete(@RequestParam String key) {
        APICustomize<String> response = variantGroupService.delete(key);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
