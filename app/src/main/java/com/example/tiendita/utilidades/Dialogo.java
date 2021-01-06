package com.example.tiendita.utilidades;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.tiendita.R;

public class Dialogo {
   public static void muestraDialogoProceso(View view, AlertDialog alertDialog, int idRecursoMensaje) {
      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
      View vDialogoProceso = LayoutInflater.from(view.getContext())
         .inflate(R.layout.dialogo_proceso, null);
      TextView tv = vDialogoProceso.findViewById(R.id.tv_proceso);
      tv.setText(idRecursoMensaje);

      alertDialogBuilder.setView(vDialogoProceso);
      alertDialog = alertDialogBuilder.create();
      alertDialog.show();
   }

   public static void ocultaDialogoProceso(AlertDialog alertDialog) {
      alertDialog.dismiss();
   }
}
