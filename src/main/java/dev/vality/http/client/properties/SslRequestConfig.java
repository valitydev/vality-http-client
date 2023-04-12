package dev.vality.http.client.properties;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SslRequestConfig {

    private String certPath;
    private String certType;
    private String certPass;

}
