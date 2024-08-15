package com.ocrf.bff.config.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Service {
    private String domain;
    private String port;
    private String prefix;
}
