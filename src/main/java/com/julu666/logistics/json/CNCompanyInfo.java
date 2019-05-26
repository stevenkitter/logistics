package com.julu666.logistics.json;

import lombok.Data;

import java.io.Serializable;

@Data
public class CNCompanyInfo implements Serializable {

    private String companyCode;
    private String companyName;
    private String iconUrl100x100;
    private String iconUrl102x38;
    private String iconUrl16x16;
    private String iconUrl288x72;
    private String iconUrl36x36;
    private String iconUrl56x56;
    private String pinyin;
    private String serviceTel;
    private String webUrl;


}
