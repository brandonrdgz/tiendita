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

}
