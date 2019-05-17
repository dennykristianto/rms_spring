package com.mitrais.rms.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String store(String subFolder, MultipartFile file);
}
