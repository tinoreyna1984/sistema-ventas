package com.tinexlab.svbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.tinexlab.svbackend.model.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetPasswordToken(String resetPasswordToken);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.userStatus = 'APPROVED', u.approvalDate = CURRENT_TIMESTAMP WHERE u.id = :id")
    void approveUser(@Param(value = "id") Long id);

    // implementación con clases Custom
    @Query("SELECT u.failedAttempts FROM User u WHERE u.username = ?1")
    public int getFailedAttempts(String username);
    @Query("SELECT u.accountNonLocked FROM User u WHERE u.username = ?1")
    public boolean isAccountNotLocked(String username);

    // actualiza usuario con token generado
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.resetPasswordToken = :token WHERE u.email = :email")
    void updateResetPasswordToken(@Param(value = "email")String email, @Param(value = "token")String token);

    // dashboard
    @Query("SELECT count(*) FROM User u")
    public long totalUsers();
    //@Query("SELECT count(*) FROM User u WHERE u.accountNonExpired = true")
    @Query(value = "SELECT totalActiveUsers()", nativeQuery = true)
    public long totalActiveUsers();
    //@Query("SELECT count(*) FROM User u WHERE u.accountNonLocked = false")
    @Query(value = "select totalLockedUsers()", nativeQuery = true)
    public long totalLockedUsers();
    
    @Query(value = "SELECT approvedUsers()", nativeQuery = true)
    long approvedUsers();
    @Query(value = "SELECT operatorsCount()", nativeQuery = true)
    long operatorsCount();
    @Query(value = "SELECT managersCount()", nativeQuery = true)
    long managersCount();

    // asigna caja
    @Query(value = "select count(*) from usuario_caja where asignado_por=:asignado_por", nativeQuery = true)
    long asignadoPor(@Param(value = "asignado_por") String asignado_por);
    @Query(value = "select count(*) from usuario_caja where caja_id=:caja_id and usuario_id=:usuario_id", nativeQuery = true)
    long verificaUsuariosCajas(@Param(value = "usuario_id") Long usuario_id, @Param(value = "caja_id") Long caja_id);
    @Query(value = "select count(*) from usuario_caja where usuario_id=:usuario_id", nativeQuery = true)
    long limiteCajaPorUsuario(@Param(value = "usuario_id") Long usuario_id);
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO usuario_caja(usuario_id, caja_id, asignado_por) VALUES(:usuario_id, :caja_id, :asignado_por)", nativeQuery = true)
    void agregaUsuarioACaja(
        @Param(value = "usuario_id") Long usuario_id, 
        @Param(value = "caja_id") Long caja_id, 
        @Param(value = "asignado_por") String asignado_por
    );
}
