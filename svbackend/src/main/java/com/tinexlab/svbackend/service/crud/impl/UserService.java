package com.tinexlab.svbackend.service.crud.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.tinexlab.svbackend.model.dto.request.UserCajaRequest;
import com.tinexlab.svbackend.model.dto.request.UserRequest;
import com.tinexlab.svbackend.model.dto.response.GenericResponse;
import com.tinexlab.svbackend.model.entity.Caja;
import com.tinexlab.svbackend.model.entity.User;
import com.tinexlab.svbackend.repository.CajaRepository;
import com.tinexlab.svbackend.repository.UserRepository;
import com.tinexlab.svbackend.service.crud.GenericService;
import com.tinexlab.svbackend.util.enums.Role;
import com.tinexlab.svbackend.util.enums.UserStatus;
import com.tinexlab.svbackend.util.helpers.HelperClass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@SuppressWarnings({"null"})
public class UserService implements GenericService<User, UserRequest> {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CajaRepository cajaRepository;
    private final HelperClass helperClass = new HelperClass();

    @Override
    public GenericResponse<?> get(Integer page, Integer size){
        try{
            if (page != null && size != null) {
                // Si se proporcionan los parámetros de paginación, devuelve una lista paginada
                Pageable pageable = PageRequest.of(page, size);
                Page<User> pageResult = userRepository.findAll(pageable);
                return GenericResponse.getResponse(200, "Se encuentran los usuarios", pageResult);
            } else {
                // Si no se proporcionan los parámetros de paginación, devuelve una lista completa
                List<User> users = userRepository.findAll();
                return GenericResponse.getResponse(200, "Se encuentran los usuarios", users);
            }
        } catch (DataAccessException e){
            return GenericResponse
                    .getResponse(500,
                            "Error al consultar usuarios: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()),
                            null);
        } catch (Exception e){
            return GenericResponse
                    .getResponse(500,
                            "Error inesperado: " + e.getMessage(),
                            null);
        }
    }

    @Override
    public GenericResponse<User> getById(Long id){
        Optional<User> optionalUser = userRepository.findById(id);
        User usuario;
        try {
            if(optionalUser.isEmpty()){
                return GenericResponse
                        .getResponse(400,
                                "No se encuentra el usuario con ID " + id,
                                null);
            }
            usuario = optionalUser.get();
        }catch(DataAccessException e) {
            return GenericResponse
                    .getResponse(500,
                            "Error al buscar usuario: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()),
                            null);
        } catch (Exception e){
            return GenericResponse
                    .getResponse(500,
                            "Error inesperado: " + e.getMessage(),
                            null);
        }
        return GenericResponse.getResponse(200, "Usuario encontrado", usuario);
    }

    @Override
    public GenericResponse<?> save(UserRequest request, BindingResult result){
        // verificar que el nombre de usuario no haya sido previamente usado
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
        if(optionalUser.isPresent()){
            return GenericResponse
                    .getResponse(400,
                            "Ya existe usuario con nombre de sesión " + request.getUsername(),
                            null);
        }

        User usuarioNuevo = new User();

        // si no viaja el ROL, por defecto debe ser el de USUARIO
        if(request.getRole() == null)
            request.setRole(Role.USER);

        // proceso de validación
        String errors = helperClass.validaRequest(result);
        if (!errors.isEmpty())
            return GenericResponse.getResponse(400, "Error al crear usuario", errors);

        // encripta clave
        helperClass.encriptarClaveUserRequest(request);

        usuarioNuevo.setEmail(request.getEmail());
        usuarioNuevo.setUsername(request.getUsername());
        usuarioNuevo.setPassword(request.getPassword());
        usuarioNuevo.setRole(request.getRole());
        usuarioNuevo.setUserCreator(request.getUserCreator());
        usuarioNuevo.setUserStatus(UserStatus.NOT_APPROVED);

        try {
            usuarioNuevo = userRepository.save(usuarioNuevo);
        } catch(DataAccessException e) {
            return GenericResponse
                    .getResponse(500,
                            "Error al crear usuario: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()),
                            null);
        } catch (Exception e){
            return GenericResponse
                    .getResponse(500,
                            "Error inesperado: " + e.getMessage(),
                            null);
        }

        return GenericResponse.getResponse(201, "Usuario creado", usuarioNuevo);
    }

    @Override
    public GenericResponse<?> update(UserRequest request, Long id, BindingResult result){
        // proceso de validación
        String errors = helperClass.validaRequest(result);
        if (!errors.isEmpty())
            return GenericResponse.getResponse(400, "Error al actualizar usuario", errors);

        Optional<User> optionalUser = userRepository.findById(id);
        User usuarioEditado = null;
        if(optionalUser.isEmpty())
            return GenericResponse
                    .getResponse(400,
                            "No se encuentra el usuario con ID " + id,
                            null);
        User usuarioActual = optionalUser.get();
        try {
            usuarioActual.setEmail(request.getEmail());
            usuarioActual.setUsername(request.getUsername());
            usuarioActual.setPassword(request.getPassword());
            // si no viaja el ROL, por defecto debe ser el de USUARIO
            if(request.getRole() == null)
                usuarioActual.setRole(Role.USER);
            else
                usuarioActual.setRole(request.getRole());
            // encripta clave
            helperClass.encriptarClaveUsuario(usuarioActual);
            usuarioActual.setUserCreator(request.getUserCreator());
            usuarioActual.setUserStatus(request.getUserStatus());
            usuarioEditado = userRepository.save(usuarioActual);
        } catch(DataAccessException e) {
            return GenericResponse
                    .getResponse(500,
                            "Error al actualizar usuario: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()),
                            null);
        } catch (Exception e){
            return GenericResponse
                    .getResponse(500,
                            "Error inesperado: " + e.getMessage(),
                            null);
        }
        return GenericResponse.getResponse(200, "Usuario actualizado", usuarioEditado);
    }

    @Override
    public GenericResponse<?> delete(Long id){
        try {
            userRepository.deleteById(id);
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

        return GenericResponse.getResponse(200, "Usuario borrado", null);
    }

    public GenericResponse<?> dashboard(){
        Map<String, Object> dashboard = new HashMap<>();
        long approvedUsers = 0L;
        long operatorsCount = 0L;
        long managersCount = 0L;
        try {
            approvedUsers = userRepository.approvedUsers();
            operatorsCount = userRepository.operatorsCount();
            managersCount = userRepository.managersCount();
            dashboard.put("aprobados", approvedUsers);
            dashboard.put("cajeros", operatorsCount);
            dashboard.put("gestores", managersCount);
            return GenericResponse.getResponse(200, "Dashboard", dashboard);
        }catch(DataAccessException e) {
            return GenericResponse
                    .getResponse(500,
                            "Error al obtener valores del dashboard: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()),
                            null);
        } catch (Exception e){
            return GenericResponse
                    .getResponse(500,
                            "Error inesperado: " + e.getMessage(),
                            null);
        }
    }

    public GenericResponse<?> asignadoPor(String asignadoPor){
        long asignados = 0L;
        try {
            asignados = userRepository.asignadoPor(asignadoPor);
            return GenericResponse.getResponse(200, "Dashboard", asignados);
        }catch(DataAccessException e) {
            return GenericResponse
                    .getResponse(500,
                            "Error al obtener valores de asignacion: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()),
                            null);
        } catch (Exception e){
            return GenericResponse
                    .getResponse(500,
                            "Error inesperado: " + e.getMessage(),
                            null);
        }}

    public GenericResponse<?> approve(Long id){
        try {
            userRepository.approveUser(id);
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

        return GenericResponse.getResponse(200, "Se aprueba al usuario", null);
    }

    public GenericResponse<?> asignaCaja(UserCajaRequest request){
        User user = userRepository.findById(request.getUserId()).orElseThrow();
        Caja caja = cajaRepository.findById(request.getCajaId()).orElseThrow();
        if(userRepository.limiteCajaPorUsuario(request.getUserId()) >= 2){
            return GenericResponse
                    .getResponse(400,
                            "El usuario " + user.getUsername() + " no debe tener más de 2 cajas asignadas.",
                            null);
        }
        if(userRepository.verificaUsuariosCajas(request.getUserId(), request.getCajaId()) > 0L){
            return GenericResponse
                    .getResponse(400,
                            "El usuario " + user.getUsername() + " ya tiene asignada la caja " + caja.getDescripcion(),
                            null);
        }
        try {
            userRepository.agregaUsuarioACaja(request.getUserId(), request.getCajaId(), request.getAssignedBy());
        }catch(DataAccessException e) {
            return GenericResponse
                    .getResponse(500,
                            "Error al realizar la operación en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()),
                            null);
        } catch (Exception e){
            return GenericResponse
                    .getResponse(500,
                            "Error inesperado: " + e.getMessage(),
                            null);
        }
        return GenericResponse.getResponse(200, "El usuario " + user.getUsername() + " es asignado a la caja " + caja.getDescripcion(), null);
    }

    public GenericResponse<?> cargarDesdeCSV(MultipartFile archivo) throws IOException {
        try (
             Reader reader = new InputStreamReader(archivo.getInputStream());
             CSVReader csvReader = new CSVReaderBuilder(reader).build()
        ) {
            List<String[]> filas = csvReader.readAll();

            for (String[] fila : filas) {
                UserRequest userRequest = pasarValores(fila);
                User user = new User();
                user.setUsername(userRequest.getUsername());
                helperClass.encriptarClaveUserRequest(userRequest);
                user.setPassword(userRequest.getPassword());
                user.setEmail(userRequest.getEmail());
                user.setRole(userRequest.getRole());
                user.setUserCreator(userRequest.getUserCreator());
                user.setUserStatus(UserStatus.NOT_APPROVED);
                userRepository.save(user);
            }
            return GenericResponse.getResponse(201, "Se creó usuarios desde CSV", archivo.getName());
        } catch (CsvException e) {
            return GenericResponse
                    .getResponse(400,
                            "Error al cargar usuarios desde CSV, revisar archivo ",
                            null);
        } catch (DataAccessException e){
            return GenericResponse
                    .getResponse(500,
                            "Error al cargar usuarios desde CSV: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()),
                            null);
        } catch (Exception e){
            return GenericResponse
                    .getResponse(500,
                            "Error inesperado: " + e.getMessage(),
                            null);
        }
    }

    private UserRequest pasarValores(String[] fila) {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername(fila[0]);
        userRequest.setPassword(fila[1]);
        userRequest.setEmail(fila[2]);
        userRequest.setRole(Role.valueOf(fila[3]));
        userRequest.setUserCreator((fila[4]));
        return userRequest;
    }

}
