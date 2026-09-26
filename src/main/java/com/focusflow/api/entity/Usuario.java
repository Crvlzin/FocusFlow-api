package com.focusflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "idUsuario")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario", updatable = false, nullable = false)
    private UUID idUsuario;

    @Column(name = "nm_usuario", nullable = false, length = 150)
    private String nmUsuario;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Column(name = "role", nullable = false, length = 50)
    @Builder.Default
    private String role = "ROLE_USER";

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private OffsetDateTime dtCriacao;

    @PrePersist
    public void prePersist() {
        if (this.dtCriacao == null) {
            this.dtCriacao = OffsetDateTime.now();
        }
        if (this.role == null) {
            this.role = "ROLE_USER";
        }
    }

    // --- Métodos exigidos pela interface UserDetails do Spring Security ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getPassword() {
        return this.senhaHash;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
