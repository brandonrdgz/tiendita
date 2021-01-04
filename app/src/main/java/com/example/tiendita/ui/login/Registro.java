package com.example.tiendita.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.tiendita.R;

public class SignUp extends AppCompatActivity {
    //Componentes
    Switch switchUser;
    LinearLayout layoutUsuario, layoutNegocio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        asignarComponentes();
        switchUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    layoutNegocio.setVisibility(View.VISIBLE);
                    layoutUsuario.setVisibility(View.GONE);
                }else{
                    layoutUsuario.setVisibility(View.VISIBLE);
                    layoutNegocio.setVisibility(View.GONE);
                }
            }
        });
    }
    private void asignarComponentes(){
        switchUser = findViewById(R.id.switchUser);
        layoutUsuario = findViewById(R.id.LayoutUsuario);
        layoutNegocio = findViewById(R.id.LayoutNegocio);
        layoutNegocio.setVisibility(View.INVISIBLE);
    }
    private void toast(String mensaje){
        Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show();
    }
}