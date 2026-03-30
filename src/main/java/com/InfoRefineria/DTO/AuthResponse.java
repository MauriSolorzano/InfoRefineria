package com.InfoRefineria.DTO;

public class AuthResponse {
    private String token;
    private String username;
    private String rol;
    private String planta;
    private String nombrePlanta;

    public AuthResponse(String token, String username, String rol, String planta, String nombrePlanta) {
        this.token = token;
        this.username = username;
        this.rol = rol;
        this.planta = planta;
        this.nombrePlanta = nombrePlanta;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRol() { return rol; }
    public String getPlanta() { return planta; }
    public String getNombrePlanta() { return nombrePlanta; }
}
