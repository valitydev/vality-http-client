package com.rbkmoney.http.client.properties;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SslRequestConfig {

    private String certFileName;
    private String certType;
    private String certPass;

}
