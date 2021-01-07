package com.example.tiendita.ui.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.tiendita.MainActivity;
import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFirebaseAuth;
import com.example.tiendita.datos.firebase.FirebaseCallback;
import com.example.tiendita.text_watcher.CampoTextWatcher;
import com.example.tiendita.utilidades.Constantes;
import com.example.tiendita.utilidades.Dialogo;
import com.example.tiendita.utilidades.ExcepcionUtilidades;
import com.example.tiendita.utilidades.Validaciones;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class InicioSesion extends AppCompatActivity {
    //Componentes
    private MaterialButton mbRegistrarse, mbIngresar;
    private TextInputLayout tilCorreo, tilContrasenia;
    private TextView tvRecuperaContrasenia;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);
        iniComponentes();
    }

    private void iniComponentes() {
        mbIngresar = findViewById(R.id.mb_ingresar);
        mbRegistrarse = findViewById(R.id.mb_registrarse);
        tilCorreo = findViewById(R.id.til_correo_inicio_sesion);
        tilContrasenia = findViewById(R.id.til_contrasenia_inicio_sesion);
        tvRecuperaContrasenia = findViewById(R.id.tv_recupera_contrasenia);

        tilCorreo.getEditText().addTextChangedListener(new CampoTextWatcher(this, tilCorreo));

        mbRegistrarse.setOnClickListener(view -> {
            limpiaCampos();
               startActivity(new Intent(InicioSesion.this, Registro.class));
           }
        );

        mbIngresar.setOnClickListener(view -> {
            limpiaCampos();
            mbIngresarClic(view);
        });

        tvRecuperaContrasenia.setOnClickListener(view -> {
            limpiaCampos();
               startActivity(new Intent(InicioSesion.this, RestablecerContrasenia.class));
           }
        );
    }

    private void mbIngresarClic(View view) {
        if(campoCorreoValido()) {
            String correo = tilCorreo.getEditText().getText().toString();
            String contrasenia = tilContrasenia.getEditText().getText().toString();

            iniciarSesion(view, correo, contrasenia);
        }
    }

    private boolean campoCorreoValido() {
        Validaciones.validaCampo(this, tilCorreo);
        Validaciones.validaCampo(this, tilContrasenia);

        return (tilCorreo.getError() == null || tilContrasenia.getError() == null);
    }

    private void iniciarSesion(View view, String correo, String contrasenia) {
        AccionesFirebaseAuth.inicioSesion(correo, contrasenia, new FirebaseCallback<Void>() {
            @Override
            public void enInicio() {
                alertDialog = Dialogo.dialogoProceso(view, R.string.msj_iniciando_sesion);
                Dialogo.muestraDialogoProceso(alertDialog);
            }

            @Override
            public void enExito(Void respuesta, int accion) {
                Dialogo.ocultaDialogoProceso(alertDialog);
                startActivity(new Intent(InicioSesion.this, MainActivity.class));
            }

            @Override
            public void enFallo(Exception excepcion) {
                Dialogo.ocultaDialogoProceso(alertDialog);
                ExcepcionUtilidades.muestraMensajeError(view, excepcion, R.string.msj_error_inicio_sesion,
                   Constantes.ETIQUETA_INICIO_SESION);
            }
        });
    }

    private void limpiaCampos(){
        tilCorreo.setError(null);
        tilContrasenia.setError(null);
    }
}