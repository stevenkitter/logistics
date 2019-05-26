package com.julu666.logistics.json;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CNData implements Serializable {
    private Long buyerUserId;
    private Boolean canEnterPackageMapMode;
    private CNCompanyInfo cpCompanyInfo;
    private String deliveryName;
    private String logisticStatusDesc;
    private String logisticsOrderGmtCreate;
    private String mailNo;
    private String operatorContact;
    private Integer packageCategory;
    private PackageStatus packageStatus;
    private String partnerCode;
    private String partnerContactPhone;
    private String partnerIconUrl;
    private String partnerName;
    private String pingjiaUrl;
    private String schoolStation;
    private String sentScanTime;
    private List<Transit> transitList;
}
