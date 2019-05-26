package com.julu666.logistics.jpa;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "packages")
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mail_no", unique = true)
    private String mailNo;

    private Long companyId;

    private String departureName;

    @Column(name = "description")
    private String description;

    private String destinationName;
    private String newStatusCode;
    private String newStatusDesc;
    private String progressbar;
    private String status;
    private String statusCode;

    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name = "mailId",referencedColumnName = "id", insertable=false, updatable=false)
    private List<Transit> transits;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "companyId", referencedColumnName = "id", insertable=false, updatable=false)
    private Company company;
}
