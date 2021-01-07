package com.example.tiendita.datos.firebase;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.tiendita.datos.modelos.UsuarioBaseModelo;
import com.example.tiendita.datos.modelos.UsuarioModelo;
import com.example.tiendita.ui.login.InicioSesion;
import com.example.tiendita.utilidades.Constantes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.SQLOutput;

public class AccionesFirebaseAuth {
    public static String getUID(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.getUid();
    }

   public static <T extends UsuarioBaseModelo> void registroUsuario(T usuario,
                                                                    FirebaseCallback<Task<AuthResult>> firebaseCallback) {
      firebaseCallback.enInicio();

      FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

      firebaseAuth.createUserWithEmailAndPassword(usuario.getCorreo(), usuario.getContrasenia())
         .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
               usuario.setId(task.getResult().getUser().getUid());

               registroDatosUsuario(usuario, new FirebaseCallback<Void>() {
                  @Override
                  public void enInicio() {

                  }

                  @Override
                  public void enExito(Void respuesta, int accion) {
                     firebaseCallback.enExito(task, 0);

                  }


                  @Override
                  public void enFallo(Exception excepcion) {
                     firebaseCallback.enFallo(excepcion);
                  }
               });
            }
            else {
               firebaseCallback.enFallo(task.getException());
            }
         });
   }

   private static <T extends UsuarioBaseModelo> void registroDatosUsuario(T usuario,
                                                                          FirebaseCallback<Void> firebaseCallback) {
       firebaseCallback.enInicio();

      FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
      String nodoTipoUsuaro = usuario instanceof UsuarioModelo ? Constantes.NODO_DATOS_USUARIOS :
         Constantes.NODO_DATOS_NEGOCIOS;

      firebaseDatabase.getReference().child(nodoTipoUsuaro).child(usuario.getId()).setValue(usuario)
         .addOnSuccessListener(aVoid -> {
               firebaseCallback.enExito(null,0);
            }
         )
         .addOnFailureListener(exception -> {
            firebaseCallback.enFallo(exception);
            }
         );
   }


   public static void inicioSesion(String correo, String contrasenia, FirebaseCallback<Void> firebaseCallback) {
      firebaseCallback.enInicio();

       FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
      firebaseAuth.signInWithEmailAndPassword(correo, contrasenia)
         .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
               firebaseCallback.enExito(null,0);
            }
            else {
               firebaseCallback.enFallo(task.getException());
            }
         });
   }

   public static void restableceContrasenia(String correo, FirebaseCallback firebaseCallback) {
      firebaseCallback.enInicio();
      FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
      firebaseAuth.useAppLanguage();

      firebaseAuth.sendPasswordResetEmail(correo).addOnCompleteListener(task -> {
         if (task.isSuccessful()) {
            firebaseCallback.enExito(null, 0);
         }
         else {
            firebaseCallback.enFallo(task.getException());
         }
      });
   }

   public static void actualizaContrasenia(String contrasenia){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().updatePassword(contrasenia);
    }

    public static void cerrarSesion(Activity activity){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(activity, InicioSesion.class);
        activity.startActivity(intent);
        activity.finish();
    }
}
