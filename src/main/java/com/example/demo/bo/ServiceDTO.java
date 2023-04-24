package com.example.demo.bo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServiceDTO {
    public ServiceDTO(String key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public ServiceDTO() {
    }
    private String key;

    private Integer value;
}
