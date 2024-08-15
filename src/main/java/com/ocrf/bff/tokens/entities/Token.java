package com.ocrf.bff.tokens.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bff_token")
public class Token {

    @Id
    @Column(name = "access_token", nullable = false, length = 2000)
    private String accessToken;

    @Column(name = "access_token_expires_in")
    private int accessTokenExpiresIn;


    @Column(name = "refresh_token", nullable = false, length = 2000)
    private String refreshToken;


    @Column(name = "last_visit")
    private LocalDateTime lastVisit;
}
