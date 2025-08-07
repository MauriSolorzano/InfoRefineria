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

    public Imagen() {
    }

    public Imagen(Long id, String nombreArchivo, Sector sector) {
        Id = id;
        this.nombreArchivo = nombreArchivo;
        this.sector = sector;
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
}
