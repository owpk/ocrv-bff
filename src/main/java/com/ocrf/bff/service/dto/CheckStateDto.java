package com.ocrf.bff.service.dto;

import com.ocrf.bff.service.dto.enums.SocketMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckStateDto {

    private SocketMessageType messageType;

    private String message;

}