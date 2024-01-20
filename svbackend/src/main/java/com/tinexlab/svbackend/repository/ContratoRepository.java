package com.tinexlab.svbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinexlab.svbackend.model.entity.Contrato;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {
}
