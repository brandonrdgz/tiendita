package com.example.tiendita.utilidades;

import android.app.Activity;
import android.text.TextUtils;

import com.example.tiendita.R;
import com.google.android.material.textfield.TextInputLayout;

public class Validaciones {
   public static void validaCampos(Activity activity, TextInputLayout[] textInputLayouts) {
      for (TextInputLayout textInputLayout : textInputLayouts) {
         validaCampo(activity, textInputLayout);
      }
   }

   public static void validaCampo(Activity activity, TextInputLayout textInputLayout) {
      String texto = textInputLayout.getEditText().getText().toString();

      if(TextUtils.isEmpty(texto)) {
         textInputLayout.setError(activity.getString(R.string.msj_error_campo_vacio));
      }
      else {
         validaCampoEspecifico(activity, textInputLayout, texto);
      }
   }

   private static void validaCampoEspecifico(Activity activity, TextInputLayout textInputLayout, String texto) {
      switch (textInputLayout.getId()) {
         case R.id.til_correo_inicio_sesion:
         case R.id.til_correo_registro:
            if(texto.matches(Constantes.EXP_REG_CORREO)) {
               textInputLayout.setError("");
            }
            else {
               textInputLayout.setError(activity.getString(R.string.msj_error_campo_correo));
            }
            break;

         case R.id.til_contrasenia_registro:
         case R.id.til_contrasenia_perfil:
            if(texto.matches(Constantes.EXP_REG_CARACTERES_NO_VALIDOS) ||
               texto.length() < Constantes.LONGITUD_MIN_CONTRASENIA) {
               textInputLayout.setError(activity.getString(R.string.msj_error_campo_contrasenia));
            }
            else if (texto.matches(Constantes.EXP_REG_LETRAS_MINUSCULAS) &&
               texto.matches(Constantes.EXP_REG_LETRAS_MAYUSCULAS) &&
               texto.matches(Constantes.EXP_REG_NUMEROS) &&
               texto.matches(Constantes.EXP_REG_CARAC_ESPECIALES) &&
               texto.length() >= Constantes.LONGITUD_MIN_CONTRASENIA) {
               textInputLayout.setError("");
            }
            break;

         case R.id.til_nombre_registro:
         case R.id.til_nombre_perfil:
            if (texto.toUpperCase().matches(Constantes.EXP_REG_NOMBRE)) {
               textInputLayout.setError("");
            }
            else {
               textInputLayout.setError(activity.getString(R.string.msj_error_campo_nombre));
            }
            break;

         case R.id.til_nombre_negocio_registro:
         case R.id.til_nombre_negocio_perfil:
            textInputLayout.setError("");
            break;

         case R.id.til_apellido_registro:
         case R.id.til_apellido_perfil:
            if (texto.toUpperCase().matches(Constantes.EXP_REG_NOMBRE)) {
               textInputLayout.setError("");
            }
            else {
               textInputLayout.setError(activity.getString(R.string.msj_error_apellido));
            }
            break;
      }
   }
}
