package com.example.tiendita.utilidades;

public class Constantes {
   public static final String EXP_REG_NOMBRE = "^[A-ZÁÉÍÓÚÑ]+(\\s[A-ZÁÉÍÓÚÑ]+)*$";
   public static final String EXP_REG_CORREO = "^[a-z0-9]+([\\-_\\.][a-z0-9]+)*@[a-z0-9]+[\\.\\-][a-z0-9]+([\\.\\-][a-z]+)*$";
   public static final String EXP_REG_LETRAS_MAYUSCULAS = ".*[A-Z]+.*";
   public static final String EXP_REG_LETRAS_MINUSCULAS = ".*[a-z]+.*";
   public static final String EXP_REG_NUMEROS = ".*[0-9]+.*";
   public static final String EXP_REG_CARAC_ESPECIALES = ".*[\\-_\\*%&=!]+.*";
   public static final String EXP_REG_CARACTERES_NO_VALIDOS = ".*[^a-zA-Z0-9\\-_\\*%&=!].*";
   public static final int LONGITUD_MIN_CONTRASENIA = 8;
   public static final String NODO_DATOS_USUARIOS = "usuarios";
   public static final String NODO_DATOS_NEGOCIOS = "negocios";
   public static final String NODO_PRODUCTOS = "productos";
   public static final String NODO_PRODUCTOS_DE_PEDIDOS = "productospedidos";
   public static final String NODO_PEDIDOS = "pedidos";
   public static final String NODO_SUCURSAL = "sucursal";

   //Constantes para atributos
   //Atributos Sucursal
   public static final String CONST_SUCURSAL_ID="sucursalID";
   public static final String CONST_NEGOCIO_ID= "negocioID";
   public static final String CONST_SUCURSAL_NOMBRE= "nombre";
   public static final String CONST_SUCURSAL_DIRECCION="direccion";
   public static final String CONST_SUCURSAL_HORAAPER= "horaAper";
   public static final String CONST_SUCURSAL_HORACIERRE= "horaCierre";
   public static final String CONST_SUCURSAL_LAT="latitud";
   public static final String CONST_SUCURSAL_LONG ="longitud";

   //Atributos BASE
   public static final String CONST_BASE_ID="id";
   public static final String CONST_BASE_NOMBRE= "nombre";
   public static final String CONST_BASE_APELLIDO= "apellido";
   public static final String CONST_BASE_CORREO= "correo";
   public static final String CONST_BASE_CONTRASENIA="contrasenia";
   public static final String CONST_BASE_LOCALIMG= "localImg";
   public static final String CONST_BASE_REMOTEIMG= "remoteImg";

   //Atributos Negocio
   public static final String CONST_NEGOCIO_NOMBRE= "nombreNegocio";
   //Atributos Pedido
   public static final String CONST_PEDIDO_ID= "pedidoID";
   public static final String CONST_PEDIDO_CLIENTE_ID="clienteID";
   public static final String CONST_PEDIDO_SUCURSAL_ID="sucursalID";
   public static final String CONST_PEDIDO_NEGOCIO_ID="negocioID";
   public static final String CONST_PEDIDO_FECHA="fecha";
   public static final String CONST_PEDIDO_HORA="hora";
   public static final String CONST_PEDIDO_PAGO="pago";
   public static final String CONST_PEDIDO_TOTAL_PROD="totalProductos";
   //Atributos Producto
   public static final String CONST_PRODUCTO_ID="productoId";
   public static final String CONST_PRODUCTO_SUCURSAL_ID="sucursalId";
   public static final String CONST_PRODUCTO_NOMBRE="nombreProducto";
   public static final String CONST_PRODUCTO_DESCRIPCION="descripcion";
   public static final String CONST_PRODUCTO_PRECIO="precio";
   public static final String CONST_PRODUCTO_CANTIDAD="cantidad";
   //Constantes para control de flujo
   //Constantes para Tipo de vista
   public static final String CONST_NEGOCIO_TYPE ="esNegocio";


   //Constantes para el tipo de actualizacion de la referencia local img
   public static int UPDATE_LOCALIMG_CLIENTE=1;
   public static int UPDATE_LOCALIMG_PRODUCTO=2;
   public static int UPDATE_LOCALIMG_NEGOCIO=3;
   public static int UPDATE_LOCALIMG_SUCURSAL=4;
   public static int UPDATE_LOCALIMG_PRODUCTO_PEDIDO=5;


}
