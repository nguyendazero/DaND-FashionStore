package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.service.MinioService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.minio.MinioClient;
import io.minio.GetObjectArgs;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;
    private static final Logger logger = LoggerFactory.getLogger(MinioServiceImpl.class);

    public MinioServiceImpl(@Value("${minio.url}") String url,
                            @Value("${minio.access-key}") String accessKey,
                            @Value("${minio.secret-key}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public InputStream getImage(String bucketName, String objectName) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public void putObject(String bucketName, String objectName, InputStream inputStream, String contentType) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, inputStream.available(), -1)
                        .contentType(contentType)
                        .build());
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public void deleteObject(String bucketName, String objectName) {
        try {
            RemoveObjectArgs args = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();
            minioClient.removeObject(args);
            logger.info("Successfully deleted object: {} from bucket: {}", objectName, bucketName);
        } catch (MinioException e) {
            logger.error("Minio error deleting object: {} from bucket: {}", objectName, bucketName, e);
            throw new RuntimeException("Could not delete object from MinIO", e);
        } catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
            logger.error("Error during deletion process for object: {} from bucket: {}", objectName, bucketName, e);
            throw new RuntimeException("Error during MinIO operation", e);
        }
    }
}