package com.tinexlab.svbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinexlab.svbackend.model.entity.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {
}
