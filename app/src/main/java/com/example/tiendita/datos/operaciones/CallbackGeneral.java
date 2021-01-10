package com.example.tiendita.datos.operaciones;

public interface CallbackGeneral<T> {
   void enInicio();
   void enExito(T respuesta);
   void enFallo(Exception excepcion);
}
