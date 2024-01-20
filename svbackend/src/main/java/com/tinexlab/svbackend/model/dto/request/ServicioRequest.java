package com.tinexlab.svbackend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicioRequest {
    private String descripcion;
    private Double precio;
    private Long dispositivoId;
}
