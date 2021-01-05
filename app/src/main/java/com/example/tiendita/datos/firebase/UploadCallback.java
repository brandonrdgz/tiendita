package com.example.tiendita.datos.firebase;

import java.io.File;

public interface UploadCallback<T> {
    void enInicioCar();
    void enExitoCar(T respuesta);
    void enFalloCar(Exception excepcion);
}
