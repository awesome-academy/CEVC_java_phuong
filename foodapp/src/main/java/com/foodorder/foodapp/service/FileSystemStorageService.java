package com.foodorder.foodapp.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.stream.Stream;

@Service
@SuppressWarnings("null")
public class FileSystemStorageService implements FilesStorageService {

  private final Path ROOT_LOCATION = Paths
      .get(System.getProperty("user.dir"), "foodapp", "src", "main", "resources", "static", "uploads").toAbsolutePath();
  private final String FOLDER = "/uploads/";

  @Override
  public void init() {
    try {
      if (!Files.exists(ROOT_LOCATION)) {
        Files.createDirectories(ROOT_LOCATION);
      }
    } catch (Exception e) {
      throw new RuntimeException("Could not initialize folder for upload!");
    }
  }

  @Override
  public String save(MultipartFile file) {
    try {
      if (!Files.exists(ROOT_LOCATION)) {
        Files.createDirectories(ROOT_LOCATION);
      }
      String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s", "_");
      Path destinationFile = ROOT_LOCATION.resolve(filename).normalize().toAbsolutePath();
      Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
      return FOLDER + filename;
    } catch (Exception e) {
      throw new RuntimeException("Failed to store file: " + e.getMessage());
    }
  }

  @Override
  public Resource load(String filename) {
    try {
      Path file = ROOT_LOCATION.resolve(filename);
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
      Path file = ROOT_LOCATION.resolve(filename);
      Files.deleteIfExists(file);
    } catch (Exception e) {
      throw new RuntimeException("Could not delete file: " + filename, e);
    }
  }

  @Override
  public Stream<Path> loadAll() {
    try {
      return Files.walk(this.ROOT_LOCATION, 1)
          .filter(path -> !path.equals(this.ROOT_LOCATION))
          .map(this.ROOT_LOCATION::relativize);
    } catch (Exception e) {
      throw new RuntimeException("Could not load files", e);
    }
  }
}
