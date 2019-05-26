package com.julu666.logistics.json;

import lombok.Data;

import java.io.Serializable;

@Data
public class Transit implements Serializable {
    private String action;
    private String message;
    private String sectionIcon;
    private String sectionName;
    private String time;

}
