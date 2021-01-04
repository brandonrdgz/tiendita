package com.example.tiendita.datos.firebase;

public interface FirebaseCallback<T> {
   void enInicio();
   void enExito(T respuesta);
   void enFallo(Exception excepcion);
}
