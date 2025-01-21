package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctImageResponse;
import com.haibazo_bff_its_rct_webapi.enums.EntityType;
import com.haibazo_bff_its_rct_webapi.service.ImageService;
import com.haibazo_bff_its_rct_webapi.service.impl.MinioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/bff/its-rct/v1")
@RequiredArgsConstructor
public class ImageController {

    private final MinioServiceImpl minioService;
    private final ImageService imageService;

    //Lay anh tu Minio
    @GetMapping("/public/image/{bucketName}/{objectName}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable String bucketName, @PathVariable String objectName) {
        try {
            InputStream imageStream = minioService.getImage(bucketName, objectName);
            InputStreamResource resource = new InputStreamResource(imageStream);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + objectName + "\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/public/image/images")
    public ResponseEntity<?> images(@RequestParam Long id,@RequestParam EntityType type){
        APICustomize<List<ItsRctImageResponse>> response = imageService.getImages(id, type);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }
}