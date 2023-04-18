package dev.vality.http.client.properties;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProxyRequestConfig {

    private String address;
    private int port;
    private String user;
    private String password;

}
