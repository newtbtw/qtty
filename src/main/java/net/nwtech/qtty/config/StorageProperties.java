package net.nwtech.qtty.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.s3")
public record StorageProperties(
        boolean enabled,
        String endpoint,
        String region,
        String accessKey,
        String secretKey,
        String bucket,
        boolean pathStyleAccess,
        boolean createBucketIfMissing,
        String publicBaseUrl
) {
}
