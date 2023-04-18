package dev.vality.http.client.properties;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SslRequestConfig {

    private CertFileInfo certFileInfo;
    private CertPlainTextInfo certPlainTextInfo;

    @Data
    @Builder
    public static class CertFileInfo {
        private String certPath;
        private String certType;
        private String certPass;
    }

    @Data
    @Builder
    public static class CertPlainTextInfo {
        private String privateCertData;
        private String publicCertData;
    }

}
