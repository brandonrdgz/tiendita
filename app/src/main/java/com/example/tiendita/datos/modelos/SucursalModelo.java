package com.example.tiendita.datos.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class SucursalModelo implements Parcelable {
    private String sucursalID;
    private String negocioID;
    private String nombre;
    private String direccion;
    private String horaAper;
    private String horaCierre;
    private String remoteImg;
    private Double latitud;
    private Double longitud;

    //Atributo estatico utilizado por la interfaz Parcelable
    public static final Creator<SucursalModelo> CREATOR = new Creator<SucursalModelo>() {
        @Override
        public SucursalModelo createFromParcel(Parcel in) {
            return new SucursalModelo(in);
        }

        @Override
        public SucursalModelo[] newArray(int size) {
            return new SucursalModelo[size];
        }
    };

    public SucursalModelo() {

    }

    //Constructor utilizado por la interfaz Parcelable
    private SucursalModelo(Parcel parcel) {
        this.sucursalID = parcel.readString();
        this.negocioID = parcel.readString();
        this.nombre = parcel.readString();
        this.direccion = parcel.readString();
        this.horaAper = parcel.readString();
        this.horaCierre = parcel.readString();
        this.remoteImg = parcel.readString();
        this.latitud = parcel.readDouble();
        this.longitud = parcel.readDouble();
    }

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

    @Override
    public String toString() {
        return "Sucursal" +
                "nombre=" + nombre + "\n" +
                "direccion=" + direccion + "\n" +
                "hora de apertura=" + horaAper + "\n"+
                "hora de cierre=" + horaCierre ;
    }

    //Métodos de la interfaz Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sucursalID);
        dest.writeString(negocioID);
        dest.writeString(nombre);
        dest.writeString(direccion);
        dest.writeString(horaAper);
        dest.writeString(horaCierre);
        dest.writeString(remoteImg);
        dest.writeDouble(latitud);
        dest.writeDouble(longitud);
    }
}
