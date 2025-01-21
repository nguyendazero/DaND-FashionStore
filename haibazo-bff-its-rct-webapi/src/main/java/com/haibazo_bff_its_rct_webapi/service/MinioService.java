package com.haibazo_bff_its_rct_webapi.service;

import java.io.InputStream;

public interface MinioService {

    InputStream getImage(String bucketName, String objectName) throws Exception;

    void putObject(String bucketName, String objectName, InputStream inputStream, String contentType) throws Exception;

    void deleteObject(String bucketName, String objectName);
}
