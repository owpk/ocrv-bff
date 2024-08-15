package com.ocrf.bff.service.dto;
import lombok.Data;

import java.util.List;

@Data
public class Userinfo {

    private String name;

    private String fio;

    private String department;

    private String telephone;

    private String telephoneSuffix;

    private String function;

    private String levelName;

    private String levelDescription;

    private List<String> authorities;
}

