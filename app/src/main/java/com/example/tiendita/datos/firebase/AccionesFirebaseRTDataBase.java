package com.example.tiendita.datos.firebase;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tiendita.R;
import com.example.tiendita.datos.bd.SQLite;
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
    public static final int GET_PRODUCTOS_ACCTION=10;

    public static void getUser(String UID,FirebaseCallback<DataSnapshot> firebaseCallback){
        firebaseCallback.enInicio();
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
                            firebaseCallback.enFallo(new Exception("El usuario no existe"));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public static void getNegocio(String UID,FirebaseCallback<DataSnapshot> firebaseCallback){
        firebaseCallback.enInicio();
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
                            firebaseCallback.enFallo(new Exception("El usuario no existe"));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public static void updateUser(UsuarioModelo usuarioModelo, FirebaseCallback firebaseCallback){
        firebaseCallback.enInicio();
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
        firebaseCallback.enInicio();
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
        firebaseCallback.enInicio();
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
    public static void insertLocalImgRef(String id, String newLocalImg, Context context){
        SQLite base= new SQLite(context);
        base.abrir();
        if(base.insertRef(id,newLocalImg)){
            Toast.makeText(context, R.string.updated_ref,Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context, R.string.error_ref,Toast.LENGTH_LONG).show();
        }
        base.cerrar();
    }
    public static void updateLocalImgRef(String id, String newLocalImg, Context context){
        SQLite base= new SQLite(context);
        base.abrir();
            if(base.actualizaRef(id,newLocalImg)){
                Toast.makeText(context, R.string.updated_ref,Toast.LENGTH_LONG).show();
        }else{
                Toast.makeText(context, R.string.error_ref,Toast.LENGTH_LONG).show();
            }
        base.cerrar();
    }
    public static String getLocalImgRef(String id, Context context){
        SQLite base= new SQLite(context);
        base.abrir();
        String ref=base.getImgRef(id);
        base.cerrar();
        return ref;
    }
    public static void getListaPedidos(String UID,boolean esNegocio,FirebaseCallback<DataSnapshot> firebaseCallback){
        firebaseCallback.enInicio();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref ;
        if(esNegocio) {
            ref = databaseReference
                    .child(Constantes.NODO_PEDIDOS)
                    .orderByChild(Constantes.CONST_PEDIDO_NEGOCIO_ID)
                    .equalTo(UID)
                    .getRef();
        }else {
            ref = databaseReference
                    .child(Constantes.NODO_PEDIDOS)
                    .orderByChild(Constantes.CONST_PEDIDO_CLIENTE_ID)
                    .equalTo(UID)
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
        firebaseCallback.enInicio();
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
        firebaseCallback.enInicio();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
         databaseReference
                    .child(Constantes.NODO_PRODUCTOS_DE_PEDIDOS)
                    .orderByChild(Constantes.CONST_PEDIDO_NEGOCIO_ID)
                    .equalTo(UID)
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
        firebaseCallback.enInicio();
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
    public static void getProductos(String UID,FirebaseCallback<DataSnapshot> firebaseCallback){
        firebaseCallback.enInicio();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_PRODUCTOS)
                .orderByChild(Constantes.CONST_PRODUCTO_SUCURSAL_ID)
                .equalTo(UID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            firebaseCallback.enExito(snapshot,GET_PRODUCTOS_ACCTION);
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
