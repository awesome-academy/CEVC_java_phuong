package com.foodorder.foodapp.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

/**
 * app.upload.*: where uploaded images are stored and the URL folder they are served from.
 *
 * @param folder   URL path prefix, e.g. "/uploads/"
 * @param location absolute or relative directory on disk
 */
@Validated
@ConfigurationProperties(prefix = "app.upload")
public record UploadProperties(@NotBlank String folder, @NotBlank String location) {
}
