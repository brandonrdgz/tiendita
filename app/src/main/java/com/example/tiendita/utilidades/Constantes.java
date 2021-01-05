package com.example.tiendita.utilidades;

public class Constantes {
   public static final String EXP_REG_NOMBRE = "^[A-ZÁÉÍÓÚÑ]+(\\s[A-ZÁÉÍÓÚÑ]+)*$";
   public static final String EXP_REG_CORREO = "^[a-z]+([-_.][a-z]+)*@[a-z]+\\.[a-z]+(\\.[a-z]+)*$";
   public static final String EXP_REG_LETRAS_MAYUSCULAS = ".*[A-Z]+.*";
   public static final String EXP_REG_LETRAS_MINUSCULAS = ".*[a-z]+.*";
   public static final String EXP_REG_NUMEROS = ".*[0-9]+.*";
   public static final String EXP_REG_CARAC_ESPECIALES = ".*[\\-_\\*%&=!]+.*";
   public static final String EXP_REG_CARACTERES_NO_VALIDOS = ".*[^a-zA-Z0-9\\-_\\*%&=!].*";
   public static final int LONGITUD_MIN_CONTRASENIA = 8;
   public static final String NODO_DATOS_USUARIOS = "usuarios";
   public static final String NODO_DATOS_NEGOCIOS = "negocios";
   public static final String NODO_PRODUCTOS = "productos";
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
   public static final String CONST_SUCURSAL_LOCALIMG= "localImg";
   public static final String CONST_SUCURSAL_REMOTEIMG= "remoteImg";
   public static final String CONST_SUCURSAL_LAT="latitud";
   public static final String CONST_SUCURSAL_LONG ="longitud";

   //Constantes para el tipo de actualizacion de la referencia local img
   public static int UPDATE_CLIENTE=1;
   public static int UPDATE_LOCALIMG_PRODUCTO=2;
   public static int UPDATE_LOCALIMG_NEGOCIO=3;
   public static int UPDATE_LOCALIMG_SUCURSAL=4;


}
