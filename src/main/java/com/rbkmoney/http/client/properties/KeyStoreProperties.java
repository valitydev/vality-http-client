package com.rbkmoney.http.client.properties;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyStoreProperties {

    private String certificateFolder;
    private String type;
    private String password;

}
