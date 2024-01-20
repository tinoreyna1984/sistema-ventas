package com.tinexlab.svbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinexlab.svbackend.model.entity.Dispositivo;

public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {
}
