package com.example.tiendita.utilidades;

import android.util.Log;
import android.view.View;

import com.example.tiendita.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class ExcepcionUtilidades {
   public static void muestraMensajeError(View view, Exception excepcion, int msjAlternativo,
                                          String etiquetaExcepcion) {
      int mensajeError = mensajeExcepcion(excepcion, etiquetaExcepcion);

      if(mensajeError != -1) {
         Snackbar.make(view, mensajeError, Snackbar.LENGTH_LONG).show();
      }
      else {
         Snackbar.make(view, msjAlternativo, Snackbar.LENGTH_LONG).show();
      }

      if(excepcion != null) {
         Log.e(etiquetaExcepcion, excepcion.getMessage());
      }
   }

   private static int mensajeExcepcion(Exception excepcion, String etiquetaExcepcion) {
      int mensajeExcepcion = -1;

      switch(etiquetaExcepcion) {
         case Constantes.ETIQUETA_REGISTRO:
            if(excepcion instanceof FirebaseAuthUserCollisionException) {
               mensajeExcepcion = R.string.msj_error_correo_registrado;
            }
            else if(excepcion instanceof FirebaseAuthInvalidCredentialsException) {
               mensajeExcepcion = R.string.msj_error_campo_correo;
            }
            break;

         case Constantes.ETIQUETA_INICIO_SESION:
            if(excepcion instanceof FirebaseAuthInvalidCredentialsException ||
               excepcion instanceof FirebaseAuthInvalidUserException) {
               mensajeExcepcion = R.string.msj_error_credenciales_no_validas;
            }
            break;
      }

      return mensajeExcepcion;
   }
}
