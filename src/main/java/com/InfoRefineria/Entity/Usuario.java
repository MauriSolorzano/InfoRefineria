package com.InfoRefineria.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String rol;

    @ManyToOne
    @JoinColumn(name = "planta_id")
    private Planta planta;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToMany
    @JoinTable(
            name = "usuario_sectores",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "sector_id")
    )
    @JsonIgnoreProperties({"planta", "usuarios"})
    private List<Sector> sectores = new ArrayList<>();

    public List<Sector> getSectores() { return sectores; }
    public void setSectores(List<Sector> sectores) { this.sectores = sectores; }

    public Usuario() {
    }

    public Usuario(Long id, String username, String passwordHash, String rol, Planta planta, Boolean activo, List<Sector> sectores) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.planta = planta;
        this.activo = activo;
        this.sectores = sectores;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Planta getPlanta() {
        return planta;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}



