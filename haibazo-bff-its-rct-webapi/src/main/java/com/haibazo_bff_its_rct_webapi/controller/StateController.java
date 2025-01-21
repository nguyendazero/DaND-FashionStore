package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddStateRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctStateResponse;
import com.haibazo_bff_its_rct_webapi.service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1")
public class StateController {

    private final StateService stateService;

    @GetMapping("/public/state/states")
    public ResponseEntity<?> states(@RequestParam String countryCode) {
        APICustomize<List<ItsRctStateResponse>> response = stateService.states(countryCode);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/state")
    public ResponseEntity<?> state(@RequestParam Long id) {
        APICustomize<ItsRctStateResponse> response = stateService.state(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/state")
    public ResponseEntity<?> add(@RequestBody AddStateRequest request) {
        APICustomize<ItsRctStateResponse> response = stateService.add(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PutMapping("/admin/state")
    public ResponseEntity<?> update(@RequestParam Long id, @RequestBody AddStateRequest request) {
        APICustomize<ItsRctStateResponse> response = stateService.update(id, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/state")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        APICustomize<String> response = stateService.delete(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
