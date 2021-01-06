package com.example.tiendita.datos.firebase;

import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tiendita.datos.modelos.NegocioModelo;
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
    public static final int GET_USER_ACCTION=1;
    public static final int GET_NEGOCIO_ACCTION=2;
    public static final int UPDATE_USER_ACCTION=3;
    public static final int UPDATE_NEGOCIO_ACCTION=4;
    public static final int GET_NEAR_SUCURSAL_ACCTION=5;
    public static final int GET_LISTA_PEDIDOS_ACCTION=6;
    public static final int GET_PEDIDO_ACCTION=7;
    public static final int GET_LISTA_PRODUCTOS_PEDIDO_ACCTION=8;
    public static final int GET_SUCURSAL_ACCTION=9;

    public static void getUser(String UID,FirebaseCallback<DataSnapshot> firebaseCallback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_DATOS_USUARIOS)
                .child(UID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            firebaseCallback.enExito(snapshot,GET_USER_ACCTION);
                        }else{
                            firebaseCallback.enFallo(null);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public static void getNegocio(String UID,FirebaseCallback<DataSnapshot> firebaseCallback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_DATOS_NEGOCIOS)
                .child(UID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            firebaseCallback.enExito(snapshot,GET_NEGOCIO_ACCTION);
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
                    firebaseCallback.enExito(null,UPDATE_USER_ACCTION);
                }else {
                    firebaseCallback.enFallo(null);
                }
            }
        });
    }
    public static void updateNegocio(NegocioModelo negocioModelo, FirebaseCallback firebaseCallback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_DATOS_NEGOCIOS)
                .child(negocioModelo.getId())
                .setValue(negocioModelo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            firebaseCallback.enExito(null, UPDATE_NEGOCIO_ACCTION);
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
                                firebaseCallback.enExito(snapshot,GET_NEAR_SUCURSAL_ACCTION);
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
                ref = databaseReference
                        .child(Constantes.NODO_PRODUCTOS)
                        .child(id);
                break;
            case 3 :
                ref = databaseReference
                        .child(Constantes.NODO_DATOS_NEGOCIOS)
                        .child(id);
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
    public static void getListaPedidos(String UID,boolean esNegocio,FirebaseCallback<DataSnapshot> firebaseCallback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref ;
        if(esNegocio) {
            ref = databaseReference
                    .child(Constantes.NODO_PEDIDOS)
                    .orderByChild(Constantes.CONST_PEDIDO_NEGOCIO_ID)
                    .equalTo(UID)
                    .orderByKey()
                    .getRef();
        }else {
            ref = databaseReference
                    .child(Constantes.NODO_PEDIDOS)
                    .orderByChild(Constantes.CONST_PEDIDO_CLIENTE_ID)
                    .equalTo(UID)
                    .orderByKey()
                    .getRef();
        }
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            firebaseCallback.enExito(snapshot, GET_LISTA_PEDIDOS_ACCTION);
                        }else{
                            firebaseCallback.enFallo(null);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public static void getPedido(String UID,FirebaseCallback<DataSnapshot> firebaseCallback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_PEDIDOS)
                .child(UID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            firebaseCallback.enExito(snapshot,GET_PEDIDO_ACCTION);
                        }else{
                            firebaseCallback.enFallo(null);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public static void getListaProductosPedido(String UID,FirebaseCallback<DataSnapshot> firebaseCallback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
         databaseReference
                    .child(Constantes.NODO_PRODUCTOS_DE_PEDIDOS)
                    .orderByChild(Constantes.CONST_PEDIDO_NEGOCIO_ID)
                    .equalTo(UID)
                    .orderByKey()
                    .getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    firebaseCallback.enExito(snapshot, GET_LISTA_PRODUCTOS_PEDIDO_ACCTION);
                }else{
                    firebaseCallback.enFallo(null);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void getSucursal(String UID,FirebaseCallback<DataSnapshot> firebaseCallback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_SUCURSAL)
                .child(UID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            firebaseCallback.enExito(snapshot,GET_PEDIDO_ACCTION);
                        }else{
                            firebaseCallback.enFallo(null);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}
