package com.julu666.logistics.json;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CNApiJSON implements Serializable {
    private String api;
    private CNData data;
    private List<String> ret;
}
