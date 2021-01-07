package com.example.tiendita.ui.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFirebaseAuth;
import com.example.tiendita.datos.firebase.FirebaseCallback;
import com.example.tiendita.text_watcher.CampoTextWatcher;
import com.example.tiendita.utilidades.Dialogo;
import com.example.tiendita.utilidades.Validaciones;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class RestablecerContrasenia extends AppCompatActivity {
   private TextInputLayout tilCorreo;
   private MaterialButton mbEnviar;
   private AlertDialog alertDialog;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_restablecer_contrasenia);
      iniComponentes();
   }

   private void iniComponentes() {
      tilCorreo = findViewById(R.id.til_correo_restablecer_contrasenia);
      tilCorreo.getEditText().addTextChangedListener(new CampoTextWatcher(this, tilCorreo));

      mbEnviar = findViewById(R.id.mb_enviar_recuperar_contrasenia);
      mbEnviar.setOnClickListener(view -> {
         mbEnviarClic(view);
      });
   }

   private void mbEnviarClic(View view) {
      if(campoCorreoValido()) {
         String correo = tilCorreo.getEditText().getText().toString();

         AccionesFirebaseAuth.restableceContrasenia(correo, new FirebaseCallback() {
            @Override
            public void enInicio() {
               alertDialog = Dialogo.dialogoProceso(view, R.string.msj_enviando_correo_restab_contra);
               Dialogo.muestraDialogoProceso(alertDialog);
            }

            @Override
            public void enExito(Object respuesta, int accion) {
               Dialogo.ocultaDialogoProceso(alertDialog);
               Snackbar.make(view, R.string.msj_correo_enviado_restab_contra, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void enFallo(Exception excepcion) {
               Dialogo.ocultaDialogoProceso(alertDialog);
            }
         });
      }
   }

   private boolean campoCorreoValido() {
      Validaciones.validaCampo(this, tilCorreo);

      return tilCorreo.getError() == null;
   }
}