package net.nwtech.qtty.adapters.out.storage;

import net.nwtech.qtty.application.port.out.ImageStoragePort;
import net.nwtech.qtty.config.StorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

@Component
public class S3ImageStorageAdapter implements ImageStoragePort {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3ImageStorageAdapter.class);

    private final S3Client s3Client;
    private final StorageProperties properties;
    private final HttpClient httpClient;

    public S3ImageStorageAdapter(S3Client s3Client, StorageProperties properties) {
        this.s3Client = s3Client;
        this.properties = properties;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public String storeFromUrl(String sourceUrl, String folder, String fileNameBase) {
        ensureStorageEnabled();
        ensureBucketExists();

        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(sourceUrl)).GET().build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() >= 400) {
                throw new IllegalStateException("Failed to download image from source URL. Status: " + response.statusCode());
            }

            String contentType = response.headers()
                    .firstValue("Content-Type")
                    .orElse("application/octet-stream");
            String objectKey = buildObjectKey(folder, fileNameBase, contentType, sourceUrl);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(properties.bucket())
                    .key(objectKey)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(response.body()));
            return objectKey;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Image transfer was interrupted", e);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to transfer image to object storage", e);
        }
    }

    @Override
    public byte[] fetch(String objectKey) {
        ensureStorageEnabled();

        ResponseBytes<?> objectBytes = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .build());

        return objectBytes.asByteArray();
    }

    @Override
    public String resolveUrl(String objectKey) {
        ensureStorageEnabled();

        if (properties.publicBaseUrl() != null && !properties.publicBaseUrl().isBlank()) {
            return properties.publicBaseUrl().replaceAll("/+$", "") + "/" + encodePath(objectKey);
        }

        String endpoint = properties.endpoint().replaceAll("/+$", "");
        if (properties.pathStyleAccess()) {
            return endpoint + "/" + properties.bucket() + "/" + encodePath(objectKey);
        }
        return endpoint + "/" + encodePath(objectKey);
    }

    private void ensureStorageEnabled() {
        if (!properties.enabled()) {
            throw new IllegalStateException("S3 storage is disabled");
        }
    }

    private void ensureBucketExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(properties.bucket()).build());
        } catch (NoSuchBucketException e) {
            createBucketIfAllowed();
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                createBucketIfAllowed();
                return;
            }
            throw e;
        }
    }

    private void createBucketIfAllowed() {
        if (!properties.createBucketIfMissing()) {
            throw new IllegalStateException("Bucket does not exist and auto creation is disabled");
        }
        LOGGER.info("Creating object storage bucket {}", properties.bucket());
        s3Client.createBucket(CreateBucketRequest.builder().bucket(properties.bucket()).build());
    }

    private String buildObjectKey(String folder, String fileNameBase, String contentType, String sourceUrl) {
        String safeFolder = sanitizePathToken(folder);
        String safeName = sanitizePathToken(fileNameBase);
        String extension = inferExtension(contentType, sourceUrl);
        return safeFolder + "/" + safeName + "-" + UUID.randomUUID() + extension;
    }

    private String sanitizePathToken(String value) {
        String normalized = Normalizer.normalize(value == null ? "image" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String sanitized = normalized.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        String compact = sanitized.replaceAll("(^-+|-+$)", "");
        return compact.isBlank() ? "image" : compact;
    }

    private String inferExtension(String contentType, String sourceUrl) {
        if (contentType.contains("jpeg")) {
            return ".jpg";
        }
        if (contentType.contains("png")) {
            return ".png";
        }
        if (contentType.contains("webp")) {
            return ".webp";
        }
        if (contentType.contains("gif")) {
            return ".gif";
        }

        int lastDot = sourceUrl.lastIndexOf('.');
        if (lastDot > -1 && lastDot < sourceUrl.length() - 1) {
            String suffix = sourceUrl.substring(lastDot);
            int queryStart = suffix.indexOf('?');
            return queryStart > -1 ? suffix.substring(0, queryStart) : suffix;
        }

        return ".bin";
    }

    private String encodePath(String objectKey) {
        return URLEncoder.encode(objectKey, StandardCharsets.UTF_8).replace("%2F", "/");
    }
}
