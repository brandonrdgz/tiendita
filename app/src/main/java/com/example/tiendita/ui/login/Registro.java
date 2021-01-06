package com.example.tiendita.ui.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tiendita.MainActivity;
import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFirebaseAuth;
import com.example.tiendita.datos.firebase.FirebaseCallback;
import com.example.tiendita.datos.modelos.NegocioModelo;
import com.example.tiendita.datos.modelos.UsuarioBaseModelo;
import com.example.tiendita.datos.modelos.UsuarioModelo;
import com.example.tiendita.text_watcher.CampoTextWatcher;
import com.example.tiendita.utilidades.Dialogo;
import com.example.tiendita.utilidades.TextInputUtilidades;
import com.example.tiendita.utilidades.Validaciones;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;

public class Registro extends AppCompatActivity {
    //Componentes
    private SwitchMaterial smTipoUsuario;
    private LinearLayout layoutTILNombreNegocio;
    private TextInputLayout tilNombre;
    private TextInputLayout tilApellido;
    private TextInputLayout tilNombreNegocio;
    private TextInputLayout tilCorreo;
    private TextInputLayout tilContrasenia;
    private TextInputLayout[] textInputLayouts;
    private MaterialButton mbRegistrar;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        iniComponentes();
    }

    private void iniComponentes() {
        smTipoUsuario = findViewById(R.id.sm_tipo_usuario);
        tilNombre = findViewById(R.id.til_nombre_registro);
        tilApellido = findViewById(R.id.til_apellido_registro);
        layoutTILNombreNegocio = findViewById(R.id.TextFieldInputTextNombreDelNegocio);
        tilNombreNegocio = findViewById(R.id.til_nombre_negocio_registro);
        tilCorreo = findViewById(R.id.til_correo_registro);
        tilContrasenia = findViewById(R.id.til_contrasenia_registro);
        mbRegistrar = findViewById(R.id.mb_registrar);

        TextInputUtilidades.soloMayusculasTextInputLayout(tilNombre);
        TextInputUtilidades.soloMayusculasTextInputLayout(tilApellido);

        textInputLayouts = new TextInputLayout[]{
                tilNombre,
                tilApellido,
                tilNombreNegocio,
                tilCorreo,
                tilContrasenia
        };

        for (TextInputLayout textInputLayout : textInputLayouts) {
            textInputLayout.getEditText().addTextChangedListener(new CampoTextWatcher(this,
                    textInputLayout));
        }

        layoutTILNombreNegocio.setVisibility(View.GONE);
        smTipoUsuario.setOnCheckedChangeListener((compoundButton, checked) -> {
            limpiaCampos();
            if (checked) {
                layoutTILNombreNegocio.setVisibility(View.VISIBLE);
            } else {
                layoutTILNombreNegocio.setVisibility(View.GONE);
            }
        });

        mbRegistrar.setOnClickListener(view -> {
                    mbRegistrarClic(view);
                }
        );
    }

    private void mbRegistrarClic(View view) {
        if (camposValidos()) {
            UsuarioBaseModelo usuario = modeloDatosUsuario();

            AccionesFirebaseAuth.registroUsuario(usuario, new FirebaseCallback<Task<AuthResult>>() {
                @Override
                public void enInicio() {
                    Dialogo.muestraDialogoProceso(view, alertDialog, R.string.msj_registrando);
                }

                @Override
                public void enExito(Task<AuthResult> respuesta, int accion) {
                    Dialogo.ocultaDialogoProceso(alertDialog);
                    Toast.makeText(view.getContext(), R.string.msj_registro_exitoso, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Registro.this, MainActivity.class));
                }

                @Override
                public void enFallo(Exception excepcion) {
                    Dialogo.ocultaDialogoProceso(alertDialog);
                }
            });
        }
    }

    private boolean camposValidos() {
        boolean camposValidos = true;
        boolean registroDeUsuario = !smTipoUsuario.isChecked();
        Validaciones.validaCampos(this, textInputLayouts);

        if (registroDeUsuario) {
            tilNombreNegocio.setError(null);
        }

        for (TextInputLayout textInputLayout : textInputLayouts) {
            if (textInputLayout.getError() != null) {
                camposValidos = false;
                break;
            }
        }

        return camposValidos;
    }

    private UsuarioBaseModelo modeloDatosUsuario() {
        String nombre = tilNombre.getEditText().getText().toString();
        String apellido = tilApellido.getEditText().getText().toString();
        String nombreNegocio = tilNombreNegocio.getEditText().toString();
        String correo = tilCorreo.getEditText().getText().toString();
        String contrasenia = tilContrasenia.getEditText().toString();

        UsuarioBaseModelo usuario = smTipoUsuario.isChecked() ? new NegocioModelo() :
                new UsuarioModelo();

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);

        if (smTipoUsuario.isChecked()) {
            ((NegocioModelo) usuario).setNombreNegocio(nombreNegocio);
        }

        usuario.setCorreo(correo);
        usuario.setContrasenia(contrasenia);

        return usuario;
    }

    private void limpiaCampos() {
        tilNombre.setError(null);
        tilApellido.setError(null);
        tilNombreNegocio.setError(null);
        tilCorreo.setError(null);
        tilContrasenia.setError(null);
    }
}