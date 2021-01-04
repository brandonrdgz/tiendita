package com.example.tiendita.ui.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.tiendita.MainActivity;
import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFirebaseAuth;
import com.example.tiendita.datos.firebase.FirebaseCallback;
import com.example.tiendita.text_watcher.CampoTextWatcher;
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
               startActivity(new Intent(InicioSesion.this, Registro.class));
           }
        );

        mbIngresar.setOnClickListener(view -> {
            mbIngresarClic(view);
        });

        tvRecuperaContrasenia.setOnClickListener(view -> {
               startActivity(new Intent(InicioSesion.this, RecuperarContrasenia.class));
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

        return tilCorreo.getError() == null;
    }

    private void iniciarSesion(View view, String correo, String contrasenia) {
        AccionesFirebaseAuth.inicioSesion(correo, contrasenia, new FirebaseCallback<Void>() {
            @Override
            public void enInicio() {
                muestraDialogoProceso(view, R.string.msj_iniciando_sesion);
            }

            @Override
            public void enExito(Void respuesta) {
                ocultaDialogoProceso();
                startActivity(new Intent(InicioSesion.this, MainActivity.class));
            }

            @Override
            public void enFallo(Exception excepcion) {
                ocultaDialogoProceso();
            }
        });
    }

    private void muestraDialogoProceso(View view, int idRecurso) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        View vDialogoProceso = LayoutInflater.from(InicioSesion.this)
           .inflate(R.layout.dialogo_proceso, null);
        TextView tv = vDialogoProceso.findViewById(R.id.tv_proceso);
        tv.setText(idRecurso);

        alertDialogBuilder.setView(vDialogoProceso);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void ocultaDialogoProceso() {
        alertDialog.dismiss();
    }
}