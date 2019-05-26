package com.julu666.logistics.json;

import lombok.Data;

import java.io.Serializable;

@Data
public class MainJson implements Serializable {
    private String mailNo;
    private String cpCode;
}
