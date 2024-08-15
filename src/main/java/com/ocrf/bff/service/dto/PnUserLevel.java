package com.ocrf.bff.service.dto;

import lombok.Getter;

@Getter
public enum PnUserLevel {
    COTEN(1,"C", "ЦОТЭН, ЦЗТ"),
    NCK(2,"L","НЦК"),
    DIRECTION_FILIAL(3,"R","ФФ, РД"),
    IONT(4,"S","ИОНТ"),
    TECH_SPEC(5,"T","Специалист тех. отдела"),
    DEPARTMENT_DIRECTOR(6,"Z","Руководитель подразделения");

    private final int priority;

    private final String value;

    private final String description;

    PnUserLevel(int priority, String value, String description) {
        this.priority = priority;
        this.value = value;
        this.description = description;
    }

    public static PnUserLevel getByValue(final String value) {
        if (value != null) {
            for (PnUserLevel level : PnUserLevel.values()) {
                if (level.getValue().equalsIgnoreCase(value)) {
                    return level;
                }
            }
        }
        return null;
    }

    public static boolean valueExist(final String value) {
        if (value != null) {
            for (PnUserLevel level : PnUserLevel.values()) {
                if (level.getValue().equalsIgnoreCase(value)) {
                    return true;
                }
            }
        }
        return false;
    }
}
