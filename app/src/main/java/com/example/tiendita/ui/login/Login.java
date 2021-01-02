package com.example.tiendita.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiendita.R;
import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {
    //Firebase
    //private FirebaseDatabase firebaseDatabase;
    //Componentes
    private Button btnRegistrarse, btnIngresar;
    private TextInputEditText usuario, contrasenia;
    private TextView recoverpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        asignarComponentes();
        this.btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, SignUp.class));
            }
        });
        this.btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarLogin(usuario.getText().toString(),contrasenia.getText().toString())){
                    toast("Ingresa a la bd a buscar com.example.tiendita.ui.usuario");
                }
            }
        });
        this.recoverpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, RecoverPassword.class));
            }
        });
    }
    private void asignarComponentes(){
        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        btnIngresar = findViewById(R.id.btnIngresar);
        usuario = findViewById(R.id.TextFieldInputTextUser);
        contrasenia = findViewById(R.id.TextFieldInputTextPassword);
        recoverpwd = findViewById(R.id.TextViewRecoverPwd);
    }
    private boolean validarLogin(String usuario, String contrasenia){
        if(usuario.isEmpty()&&contrasenia.isEmpty()){
            toast("Por favor, llene ambos campos para poder continuar");
            return false;
        }else if(usuario.isEmpty()){
            toast("El campo com.example.tiendita.ui.usuario está vacío, por favor, ingrese el com.example.tiendita.ui.usuario e intente nuevamente");
            return false;
        }else if(contrasenia.isEmpty()){
            toast("El campo contraseña está vacío, por favor, ingrese la contraseña e intente nuevamente");
            return false;
        }
        return true;
    }
    private void toast(String mensaje){
        Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show();
    }
}