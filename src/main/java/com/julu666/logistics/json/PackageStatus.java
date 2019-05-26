package com.julu666.logistics.json;

import lombok.Data;

import java.io.Serializable;

@Data
public class PackageStatus implements Serializable {
    private String departureName;
    private String desc;
    private String destinationName;
    private String newStatusCode;
    private String newStatusDesc;
    private String progressbar;
    private String status;
    private String statusCode;
}
