package com.tinexlab.svbackend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.tinexlab.svbackend.model.dto.request.PagoRequest;
import com.tinexlab.svbackend.model.dto.response.GenericResponse;
import com.tinexlab.svbackend.model.entity.Pago;
import com.tinexlab.svbackend.service.crud.impl.PagoService;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
@SuppressWarnings({"null"})
public class PagoController {
    @Autowired
    private PagoService pagoService;

    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR','ROLE_MANAGER','ROLE_USER')")
    @GetMapping("/pagos")
    // devuelve una lista completa o paginada si viajan parámetros de paginación
    public ResponseEntity<GenericResponse<?>> listarPagos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("custom-status", "OK")
                .contentType(MediaType.APPLICATION_JSON)
                .body(pagoService.get(page, size)
                );
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR','ROLE_MANAGER','ROLE_USER')")
    @GetMapping("/pagos/{id}")
    public ResponseEntity<GenericResponse<Pago>> buscarPago(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("custom-status", "OK")
                .contentType(MediaType.APPLICATION_JSON)
                .body(pagoService.getById(id)
                );
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR','ROLE_MANAGER','ROLE_USER')")
    @PostMapping("/pagos")
    public ResponseEntity<GenericResponse<?>> guardarPago(@Valid @RequestBody PagoRequest request, BindingResult result){
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("custom-status", "OK")
                .contentType(MediaType.APPLICATION_JSON)
                .body(pagoService.save(request, result)
                );
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR','ROLE_MANAGER','ROLE_USER')")
    @PutMapping("/pagos/{id}")
    public ResponseEntity<GenericResponse<?>> editarPago(@Valid @RequestBody PagoRequest request, @PathVariable Long id, BindingResult result){
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("custom-status", "OK")
                .contentType(MediaType.APPLICATION_JSON)
                .body(pagoService.update(request, id, result)
                );
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR','ROLE_MANAGER','ROLE_USER')")
    @DeleteMapping("/pagos/{id}")
    public ResponseEntity<GenericResponse<?>> borrarPago(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("custom-status", "OK")
                .contentType(MediaType.APPLICATION_JSON)
                .body(pagoService.delete(id)
                );
    }

}
