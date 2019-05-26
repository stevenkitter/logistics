package com.julu666.logistics.jpa;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

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
