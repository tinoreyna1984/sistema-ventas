package com.tinexlab.svbackend.service.crud.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.tinexlab.svbackend.model.dto.request.ContratoRequest;
import com.tinexlab.svbackend.model.dto.response.GenericResponse;
import com.tinexlab.svbackend.model.entity.Cliente;
import com.tinexlab.svbackend.model.entity.Contrato;
import com.tinexlab.svbackend.model.entity.Servicio;
import com.tinexlab.svbackend.repository.ClienteRepository;
import com.tinexlab.svbackend.repository.ContratoRepository;
import com.tinexlab.svbackend.repository.ServicioRepository;
import com.tinexlab.svbackend.service.crud.GenericService;
import com.tinexlab.svbackend.util.enums.ContractStatus;
import com.tinexlab.svbackend.util.enums.PaymentMethod;
import com.tinexlab.svbackend.util.helpers.HelperClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings({"null"})
public class ContratoService implements GenericService<Contrato, ContratoRequest> {

    @Autowired
    private ContratoRepository contratoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ServicioRepository servicioRepository;
    private final HelperClass helperClass = new HelperClass();

    @Override
    public GenericResponse<?> get(Integer page, Integer size) {
        try{
            if (page != null && size != null) {
                // Si se proporcionan los parámetros de paginación, devuelve una lista paginada
                Pageable pageable = PageRequest.of(page, size);
                Page<Contrato> pageResult = contratoRepository.findAll(pageable);
                return GenericResponse.getResponse(200, "Se encuentran los contratos", pageResult);
            } else {
                // Si no se proporcionan los parámetros de paginación, devuelve una lista completa
                List<Contrato> contratos = contratoRepository.findAll();
                return GenericResponse.getResponse(200, "Se encuentran los contratos", contratos);
            }
        } catch (DataAccessException e){
            return GenericResponse
                    .getResponse(500,
                            "Error al consultar contratos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()),
                            null);
        } catch (Exception e){
            return GenericResponse
                    .getResponse(500,
                            "Error inesperado: " + e.getMessage(),
                            null);
        }
    }

    @Override
    public GenericResponse<Contrato> getById(Long id) {
        Optional<Contrato> optionalContrato = contratoRepository.findById(id);
        Contrato contrato = null;
        try {
            if(optionalContrato.isEmpty()){
                return GenericResponse
                        .getResponse(400,
                                "No se encuentra el contrato con ID " + id,
                                null);
            }
            contrato = optionalContrato.get();
        }catch(DataAccessException e) {
            return GenericResponse
                    .getResponse(500,
                            "Error al buscar contrato: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()),
                            null);
        } catch (Exception e){
            return GenericResponse
                    .getResponse(500,
                            "Error inesperado: " + e.getMessage(),
                            null);
        }
        return GenericResponse.getResponse(200, "Contrato encontrado", contrato);
    }

    @Override
    public GenericResponse<?> save(ContratoRequest request, BindingResult result) {
        Contrato contratoNuevo = new Contrato();

        // proceso de validación
        String errors = helperClass.validaRequest(result);
        if (!errors.isEmpty())
            return GenericResponse.getResponse(400, "Error al crear contrato", errors);

        // busca cliente y servicio
        Optional<Cliente> optionalCliente = clienteRepository.findById(request.getClienteId());
        Optional<Servicio> optionalServicio = servicioRepository.findById(request.getServicioId());
        if(optionalCliente.isEmpty())
            return GenericResponse
                    .getResponse(400,
                            "No se encuentra el cliente con ID " + request.getClienteId(),
                            null);
        Cliente cliente = optionalCliente.get();
        if(optionalServicio.isEmpty())
            return GenericResponse
                    .getResponse(400,
                            "No se encuentra el servicio con ID " + request.getServicioId(),
                            null);
        Servicio servicio = optionalServicio.get();

        contratoNuevo.setFechaInicioContrato(request.getFechaInicioContrato());
        contratoNuevo.setFechaFinContrato(request.getFechaFinContrato());
        contratoNuevo.setContractStatus(ContractStatus.VIG);
        if(request.getPaymentMethod() == null)
            contratoNuevo.setPaymentMethod(PaymentMethod.TARJETA_DE_PAGO);
        else
            contratoNuevo.setPaymentMethod(request.getPaymentMethod());
        contratoNuevo.setCliente(cliente);
        contratoNuevo.setServicio(servicio);

        try {
            contratoNuevo = contratoRepository.save(contratoNuevo);
        } catch(DataAccessException e) {
            return GenericResponse
                    .getResponse(500,
                            "Error al crear contrato: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()),
                            null);
        } catch (Exception e){
            return GenericResponse
                    .getResponse(500,
                            "Error inesperado: " + e.getMessage(),
                            null);
        }

        return GenericResponse.getResponse(201, "Contrato creado", contratoNuevo);
    }

    @Override
    public GenericResponse<?> update(ContratoRequest request, Long id, BindingResult result) {
        // proceso de validación
        String errors = helperClass.validaRequest(result);
        if (!errors.isEmpty())
            return GenericResponse.getResponse(400, "Error al actualizar contrato", errors);

        Optional<Contrato> optionalContratoActual = contratoRepository.findById(id);
        Contrato contratoEditado = null;
        Contrato contratoSustitutorio = new Contrato(); // el que reemplazará al contrato actual
        if(optionalContratoActual.isEmpty())
            return GenericResponse
                    .getResponse(400,
                            "No se encuentra el contrato con ID " + id,
                            null);
        Contrato contratoActual = optionalContratoActual.get();

        // busca cliente y servicio
        Optional<Cliente> optionalCliente = clienteRepository.findById(request.getClienteId());
        Optional<Servicio> optionalServicio = servicioRepository.findById(request.getServicioId());
        if(optionalCliente.isEmpty())
            return GenericResponse
                    .getResponse(400,
                            "No se encuentra el cliente con ID " + request.getClienteId(),
                            null);
        Cliente cliente = optionalCliente.get();
        if(optionalServicio.isEmpty())
            return GenericResponse
                    .getResponse(400,
                            "No se encuentra el servicio con ID " + request.getServicioId(),
                            null);
        Servicio servicio = optionalServicio.get();

        contratoActual.setFechaInicioContrato(request.getFechaInicioContrato());
        contratoActual.setFechaFinContrato(request.getFechaFinContrato());
        contratoActual.setContractStatus(request.getContractStatus());
        contratoActual.setPaymentMethod(request.getPaymentMethod());
        contratoActual.setCliente(cliente);
        contratoActual.setServicio(servicio);

        if(request.getContractStatus() == ContractStatus.SUS){
            // Se tomará la fecha actual para el contrato sustitutorio
            LocalDateTime fechaActual = LocalDateTime.now();
            Date fechaActualDate = Date.from(fechaActual.atZone(ZoneId.systemDefault()).toInstant());

            contratoSustitutorio.setFechaInicioContrato(fechaActualDate);
            contratoSustitutorio.setFechaFinContrato(request.getFechaFinContrato());
            contratoSustitutorio.setContractStatus(ContractStatus.VIG);
            contratoSustitutorio.setPaymentMethod(request.getPaymentMethod());
            contratoSustitutorio.setCliente(cliente);
            contratoSustitutorio.setServicio(servicio);
        }

        try {
            contratoEditado = contratoRepository.save(contratoActual);
            if(request.getContractStatus() == ContractStatus.SUS) {
                contratoSustitutorio = contratoRepository.save(contratoSustitutorio);
            }
        } catch(DataAccessException e) {
            return GenericResponse
                    .getResponse(500,
                            "Error al actualizar contrato: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()),
                            null);
        } catch (Exception e){
            return GenericResponse
                    .getResponse(500,
                            "Error inesperado: " + e.getMessage(),
                            null);
        }
        return GenericResponse.getResponse(200, "Contrato actualizado", contratoEditado);
    }

    @Override
    public GenericResponse<?> delete(Long id) {
        try {
            contratoRepository.deleteById(id);
        }catch(DataAccessException e) {
            return GenericResponse
                    .getResponse(500,
                            "Error al realizar la consulta en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()),
                            null);
        } catch (Exception e){
            return GenericResponse
                    .getResponse(500,
                            "Error inesperado: " + e.getMessage(),
                            null);
        }

        return GenericResponse.getResponse(200, "Contrato borrado", null);
    }
}
