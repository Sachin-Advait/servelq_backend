package com.gis.servelq.dto;

import lombok.Data;

@Data
public class ImageUploadRequest {
    private String branchId;
    private String fileName;
    private String fileType;
    private String fileBase64;
}
