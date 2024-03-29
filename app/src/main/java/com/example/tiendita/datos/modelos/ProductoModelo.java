package com.example.tiendita.datos.modelos;

public class ProductoModelo {
    private String productoId;
    private String sucursalId;
    private String nombreProducto;
    private String descripcion;

    public String getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(String negocioId) {
        this.negocioId = negocioId;
    }

    private String negocioId;
    private float precio;
    private int cantidad;
    private String remoteImg;

    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public String getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(String sucursalId) {
        this.sucursalId = sucursalId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getRemoteImg() {
        return remoteImg;
    }

    public void setRemoteImg(String remoteImg) {
        this.remoteImg = remoteImg;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }
    @Override
    public String toString() {
        return "Producto: \n" +
                nombreProducto+ " \n"+
                "precio=$" + precio;
    }
}
