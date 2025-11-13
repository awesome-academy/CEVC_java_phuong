package com.foodorder.foodapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.stream.Stream;

@Service
@SuppressWarnings("null")
public class FileSystemStorageService implements FilesStorageService {
  private final Path rootLocation;
  private final String folder;

  public FileSystemStorageService(
      @Value("${app.upload.location}") String uploadLocation,
      @Value("${app.upload.folder}") String folder) {
    this.rootLocation = Paths.get(uploadLocation).toAbsolutePath().normalize();
    this.folder = folder;
    init();
  }

  @Override
  public void init() {
    try {
      if (!Files.exists(rootLocation)) {
        Files.createDirectories(rootLocation);
      }
    } catch (Exception e) {
      throw new RuntimeException("Could not initialize folder for upload!");
    }
  }

  @Override
  public String save(MultipartFile file) {
    try {
      if (!Files.exists(rootLocation)) {
        Files.createDirectories(rootLocation);
      }
      String originalName = Paths.get(file.getOriginalFilename()).getFileName().toString();
      String safeName = originalName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
      String filename = System.currentTimeMillis() + "_" + safeName;

      Path destinationFile = rootLocation.resolve(filename).normalize().toAbsolutePath();
      Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
      return folder + filename;
    } catch (Exception e) {
      throw new RuntimeException("Failed to store file: " + e.getMessage());
    }
  }

  @Override
  public Resource load(String filename) {
    try {
      Path file = rootLocation.resolve(filename);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new RuntimeException("Could not read file: " + filename);
      }
    } catch (Exception e) {
      throw new RuntimeException("Could not read file: " + filename, e);
    }
  }

  @Override
  public void delete(String filename) {
    try {
      Path file = rootLocation.resolve(filename);
      Files.deleteIfExists(file);
    } catch (Exception e) {
      throw new RuntimeException("Could not delete file: " + filename, e);
    }
  }

  @Override
  public Stream<Path> loadAll() {
    try {
      return Files.walk(this.rootLocation, 1)
          .filter(path -> !path.equals(this.rootLocation))
          .map(this.rootLocation::relativize);
    } catch (Exception e) {
      throw new RuntimeException("Could not load files", e);
    }
  }
}
