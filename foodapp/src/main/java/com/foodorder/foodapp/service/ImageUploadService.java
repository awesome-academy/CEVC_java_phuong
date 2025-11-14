package com.foodorder.foodapp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ImageUploadService {
  private final FilesStorageService filesStorageService;

  public String uploadImage(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }

    return filesStorageService.save(file);
  }

  public void deleteImage(String imagePath) {
    if (imagePath == null || imagePath.isEmpty()) {
      return;
    }

    String filename = extractFilenameFromPath(imagePath);
    filesStorageService.delete(filename);
  }

  private String extractFilenameFromPath(String imagePath) {
    String[] segments = imagePath.split("/");
    return segments[segments.length - 1];
  }
}
