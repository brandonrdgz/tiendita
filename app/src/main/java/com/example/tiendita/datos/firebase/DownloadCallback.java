package com.example.tiendita.datos.firebase;

import java.io.File;

public interface DownloadCallback<T> {
    void enInicioDesc();
    void enExitoDesc(T respuesta, File localFile);
    void enFalloDesc(Exception excepcion);
}
