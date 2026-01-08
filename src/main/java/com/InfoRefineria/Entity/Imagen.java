package com.InfoRefineria.Entity;

import jakarta.persistence.*;

@Entity
public class Imagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long Id;
    private String nombreArchivo;
    @Enumerated(EnumType.STRING)
    private Sector sector;
    @Enumerated(EnumType.STRING)
    private Planta planta;

    public Imagen() {
    }

    public Imagen(Long id, String nombreArchivo, Sector sector, Planta planta) {
        Id = id;
        this.nombreArchivo = nombreArchivo;
        this.sector = sector;
        this.planta = planta;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public Planta getPlanta() {
        return planta;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }
}
