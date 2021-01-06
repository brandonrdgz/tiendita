package com.example.tiendita.datos.modelos;

public class PedidoModelo {
    private String pedidoID;
    private String clienteID;
    private String sucursalID;
    private String negocioID;
    private String fecha;
    private String hora;
    private float pago;
    private int totalProductos;

    public String getPedidoID() {
        return pedidoID;
    }

    public void setPedidoID(String pedidoID) {
        this.pedidoID = pedidoID;
    }

    public String getClienteID() {
        return clienteID;
    }

    public void setClienteID(String clienteID) {
        this.clienteID = clienteID;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public float getPago() {
        return pago;
    }

    public void setPago(float pago) {
        this.pago = pago;
    }

    public int getTotalProductos() {
        return totalProductos;
    }

    public void setTotalProductos(int totalProductos) {
        this.totalProductos = totalProductos;
    }
    @Override
    public String toString() {
        return "Pedido: \n" +
                "id='" + pedidoID+ "' \n"+
                "fecha='" + fecha+ "' \n"+
                "hora='"+hora+"'";
    }
}
