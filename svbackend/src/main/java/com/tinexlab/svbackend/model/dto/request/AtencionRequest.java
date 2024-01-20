package com.tinexlab.svbackend.model.dto.request;

import com.tinexlab.svbackend.util.enums.AttentionStatus;
import com.tinexlab.svbackend.util.enums.AttentionType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtencionRequest {
    @Enumerated(EnumType.STRING)
    private AttentionType attentionType;
    @Enumerated(EnumType.STRING)
    private AttentionStatus attentionStatus;
    private Long cajaId;
    private Long clienteId;
}
