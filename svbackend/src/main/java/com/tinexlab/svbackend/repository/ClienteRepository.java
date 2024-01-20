package com.tinexlab.svbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinexlab.svbackend.model.entity.Cliente;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDocId(String docId);
}
