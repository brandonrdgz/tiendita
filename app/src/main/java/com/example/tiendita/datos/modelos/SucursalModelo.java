package com.example.tiendita.datos.modelos;

public class SucursalModelo {
    private String sucursalID;
    private String negocioID;
    private String nombre;
    private String direccion;
    private String horaAper;
    private String horaCierre;
    private String localImg;
    private String remoteImg;
    private Double latitud;
    private Double longitud;

    public String getSucursalID() {
        return sucursalID;
    }

    public void setSucursalID(String sucursalID) {
        this.sucursalID = sucursalID;
    }

    public String getNegocioID() {
        return negocioID;
    }

    public void setNegocioID(String negocioID) {
        this.negocioID = negocioID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getHoraAper() {
        return horaAper;
    }

    public void setHoraAper(String horaAper) {
        this.horaAper = horaAper;
    }

    public String getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(String horaCierre) {
        this.horaCierre = horaCierre;
    }

    public String getLocalImg() {
        return localImg;
    }

    public void setLocalImg(String localImg) {
        this.localImg = localImg;
    }

    public String getRemoteImg() {
        return remoteImg;
    }

    public void setRemoteImg(String remoteImg) {
        this.remoteImg = remoteImg;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
}
