package net.nwtech.qtty.application.port.out;

public interface ImageStoragePort {

    String storeFromUrl(String sourceUrl, String folder, String fileNameBase);

    byte[] fetch(String objectKey);

    String resolveUrl(String objectKey);
}
