package com.InfoRefineria.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "imagenes")
public class Imagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sector_id", nullable = false)
    @JsonIgnoreProperties({"imagenes", "planta", "sectores"})
    private Sector sector;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "url_publica")
    private String urlPublica;

    @Column(nullable = false)
    private Integer orden = 0;

    @Column(name = "subida_en")
    private LocalDateTime subidaEn;

    public Imagen(Long id, Sector sector, String nombreArchivo, String storagePath, String urlPublica, Integer orden, LocalDateTime subidaEn) {
        this.id = id;
        this.sector = sector;
        this.nombreArchivo = nombreArchivo;
        this.storagePath = storagePath;
        this.urlPublica = urlPublica;
        this.orden = orden;
        this.subidaEn = subidaEn;
    }

    public Imagen() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getUrlPublica() {
        return urlPublica;
    }

    public void setUrlPublica(String urlPublica) {
        this.urlPublica = urlPublica;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public LocalDateTime getSubidaEn() {
        return subidaEn;
    }

    public void setSubidaEn(LocalDateTime subidaEn) {
        this.subidaEn = subidaEn;
    }
}
