package com.tinexlab.svbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinexlab.svbackend.model.entity.Atencion;

public interface AtencionRepository extends JpaRepository<Atencion, Long> {
}
