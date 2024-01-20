package com.tinexlab.svbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinexlab.svbackend.model.entity.Servicio;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
}
