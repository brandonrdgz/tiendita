package com.example.tiendita.utilidades;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.tiendita.R;

public class Dialogo {
   public static AlertDialog dialogoProceso(View view, int idRecursoMensaje) {
      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
      View vDialogoProceso = LayoutInflater.from(view.getContext())
         .inflate(R.layout.dialogo_proceso, null);
      TextView tv = vDialogoProceso.findViewById(R.id.tv_proceso);
      tv.setText(idRecursoMensaje);

      alertDialogBuilder.setView(vDialogoProceso);

      return alertDialogBuilder.create();
   }

   public static void ocultaDialogoProceso(AlertDialog alertDialog) {
      alertDialog.dismiss();
   }
}
