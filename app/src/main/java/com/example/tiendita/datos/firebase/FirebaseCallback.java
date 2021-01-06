package com.example.tiendita.datos.firebase;

import java.io.File;

public interface FirebaseCallback<T> {
   void enInicio();
   void enExito(T respuesta,int accion);
   void enFallo(Exception excepcion);
}
