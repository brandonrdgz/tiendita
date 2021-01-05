package com.example.tiendita.datos.firebase;

import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tiendita.datos.modelos.UsuarioModelo;
import com.example.tiendita.utilidades.Constantes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class AccionesFirebaseRTDataBase {

    public static void getUser(String UID,FirebaseCallback<DataSnapshot> firebaseCallback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_DATOS_USUARIOS)
                .child(UID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            firebaseCallback.enExito(snapshot);
                        }else{
                            firebaseCallback.enFallo(null);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public static void updateUser(UsuarioModelo usuarioModelo, FirebaseCallback firebaseCallback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_DATOS_USUARIOS)
                .child(usuarioModelo.getId())
                .setValue(usuarioModelo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    firebaseCallback.enExito(null);
                }else {
                    firebaseCallback.enFallo(null);
                }
            }
        });

    }
    public static void getNearSucursales(FirebaseCallback<DataSnapshot> firebaseCallback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constantes.NODO_SUCURSAL)
                .orderByKey()
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                                firebaseCallback.enExito(snapshot);
                        }else{
                            firebaseCallback.enFallo(null);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public static void updateLocalImgRef(String id,String newLocalImg, int UPDATE_TYPE){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref=null;
        /*//Constantes para el tipo de actualizacion de la referencia local img
            public static int UPDATE_CLIENTE=1;
            public static int UPDATE_LOCALIMG_PRODUCTO=2;
            public static int UPDATE_LOCALIMG_NEGOCIO=3;
            public static int UPDATE_LOCALIMG_SUCURSAL=4;*/

        switch (UPDATE_TYPE){
            case 1 :
                ref = databaseReference
                        .child(Constantes.NODO_DATOS_USUARIOS)
                        .child(id);
                break;
            case 2 :
                break;
            case 3 :
                break;
            case 4 :
                ref = databaseReference
                        .child(Constantes.NODO_SUCURSAL)
                        .child(id);
            break;
        }
        Map<String, Object> updates = new HashMap<>();
        updates.put(Constantes.CONST_BASE_LOCALIMG, newLocalImg);
        ref.updateChildren(updates);


    }

}
