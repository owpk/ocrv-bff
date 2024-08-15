package com.ocrf.bff.tokens.repositories;

import com.ocrf.bff.tokens.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, String> {
}
