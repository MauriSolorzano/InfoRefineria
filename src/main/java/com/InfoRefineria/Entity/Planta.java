package com.InfoRefineria.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "plantas")
public class Planta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(name = "nombre_display", nullable = false)
    private String nombreDisplay;

    @Column(nullable = false)
    private Boolean activa = true;

    @Column(name = "creada_en")
    private LocalDateTime creadaEn;

    @OneToMany(mappedBy = "planta", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("planta")
    private List<Sector> sectores;

    public Planta(Long id, String nombre, String nombreDisplay, Boolean activa, LocalDateTime creadaEn, List<Sector> sectores) {
        this.id = id;
        this.nombre = nombre;
        this.nombreDisplay = nombreDisplay;
        this.activa = activa;
        this.creadaEn = creadaEn;
        this.sectores = sectores;
    }

    public Planta() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreDisplay() {
        return nombreDisplay;
    }

    public void setNombreDisplay(String nombreDisplay) {
        this.nombreDisplay = nombreDisplay;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public LocalDateTime getCreadaEn() {
        return creadaEn;
    }

    public void setCreadaEn(LocalDateTime creadaEn) {
        this.creadaEn = creadaEn;
    }

    public List<Sector> getSectores() {
        return sectores;
    }

    public void setSectores(List<Sector> sectores) {
        this.sectores = sectores;
    }
}
