package com.tinexlab.svbackend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PagoRequest {
    private Date fechaPago;
    private Long clienteId;
}
