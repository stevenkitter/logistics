package com.julu666.logistics.jpa;

import lombok.Data;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import javax.persistence.*;

@Entity
@Data
@Table(name = "transits")
public class Transit {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String action;
    private String message;
    private String sectionIcon;
    private String sectionName;
    private String time;

    private Long mailId;

}
