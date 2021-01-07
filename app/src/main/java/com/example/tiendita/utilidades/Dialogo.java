package com.example.tiendita.utilidades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.tiendita.R;

public class Dialogo {
   public static AlertDialog dialogoProceso(Context context, int idRecursoMensaje) {
      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
      View vDialogoProceso = LayoutInflater.from(context)
         .inflate(R.layout.dialogo_proceso, null);
      TextView tv = vDialogoProceso.findViewById(R.id.tv_proceso);

      tv.setText(idRecursoMensaje);
      alertDialogBuilder.setView(vDialogoProceso);
      alertDialogBuilder.setCancelable(false);

      return alertDialogBuilder.create();
   }

   public static void muestraDialogoProceso(AlertDialog alertDialog) {
      alertDialog.show();
   }

   public static void ocultaDialogoProceso(AlertDialog alertDialog) {
      alertDialog.dismiss();
   }
}
