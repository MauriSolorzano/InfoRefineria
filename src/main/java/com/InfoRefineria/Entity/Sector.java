package com.InfoRefineria.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "sectores")
public class Sector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "planta_id", nullable = false)
    @JsonIgnoreProperties("sectores")
    private Planta planta;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "nombre_display", nullable = false)
    private String nombreDisplay;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "sector", cascade = CascadeType.ALL)
    private List<Imagen> imagenes;

    public Sector(Long id, Planta planta, String nombre, String nombreDisplay, Boolean activo, List<Imagen> imagenes) {
        this.id = id;
        this.planta = planta;
        this.nombre = nombre;
        this.nombreDisplay = nombreDisplay;
        this.activo = activo;
        this.imagenes = imagenes;
    }

    public Sector() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Planta getPlanta() {
        return planta;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<Imagen> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<Imagen> imagenes) {
        this.imagenes = imagenes;
    }
}
