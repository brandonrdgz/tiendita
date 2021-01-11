package com.example.tiendita.datos.modelos;

public class ProductosPedidoModelo {
    private String pedidoID;
    private String productoId;
    private String sucursalId;
    public String getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(String negocioId) {
        this.negocioId = negocioId;
    }

    private String negocioId;
    private String nombreProducto;
    private String descripcion;
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

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getRemoteImg() {
        return remoteImg;
    }

    public void setRemoteImg(String remoteImg) {
        this.remoteImg = remoteImg;
    }

    public String getPedidoID() {
        return pedidoID;
    }

    public void setPedidoID(String pedidoID) {
        this.pedidoID = pedidoID;
    }


    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "Producto: \n" +
                 nombreProducto+ " \n"+
                "precio=$" + precio+ " \n"+
                "cantidad="+cantidad+"";
    }
    public String getDetalle(){
        return nombreProducto+ "\n"+
                "descripcion:"+descripcion+"\n"+
                "precio:$" + precio+ "\n"+
                "cantidad:"+cantidad+"\n"+
                "subtotal:$"+precio*cantidad;


    }
}
