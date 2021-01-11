package com.example.tiendita.datos.firebase;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tiendita.R;
import com.example.tiendita.datos.bd.SQLite;
import com.example.tiendita.datos.modelos.NegocioModelo;
import com.example.tiendita.datos.modelos.PedidoModelo;
import com.example.tiendita.datos.modelos.ProductoModelo;
import com.example.tiendita.datos.modelos.ProductosPedidoModelo;
import com.example.tiendita.datos.modelos.SucursalModelo;
import com.example.tiendita.datos.modelos.UsuarioModelo;
import com.example.tiendita.utilidades.Constantes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;


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
    public static final int INSERT_PEDIDO_ACCTION=11;
    public static final int INSERT_PRODUCTOS_PEDIDO_ACCTION=12;
    public static final int UPDATE_PRODUCTOS_ACCTION=13;
    public static final int DELETE_PEDIDO_ACCTION=14;
    public static final int DELETE_PRODUCTOS_PEDIDO_ACCTION=15;
    public static final int GET_SUCURSALES_ACCTION=16;
    public static final int DELETE_SUCURSAL_ACCTION=17;
    public static final int DELETE_PRODUCTOS_ACCTION=18;
    public static final int DELETE_PEDIDOS_DE_SUCURSAL_ACCTION=19;
    public static final int UPDATE_PEDIDO_ACCTION=20;
    

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
    public static void getPedido(String pedidoID,FirebaseCallback<DataSnapshot> firebaseCallback){
        firebaseCallback.enInicio();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_PEDIDOS)
                .child(pedidoID)
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
    public static void getListaProductosPedido(String pedidoID,FirebaseCallback<DataSnapshot> firebaseCallback){
        firebaseCallback.enInicio();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
         databaseReference
                    .child(Constantes.NODO_PRODUCTOS_DE_PEDIDOS)
                    .child(pedidoID)
                    .getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    firebaseCallback.enExito(snapshot, GET_LISTA_PRODUCTOS_PEDIDO_ACCTION);
                }else{
                    firebaseCallback.enFallo(new Exception("Sin productos"));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void getSucursal(String negocioID, String sucursalID,FirebaseCallback<DataSnapshot> firebaseCallback){
        firebaseCallback.enInicio();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_SUCURSAL)
                .child(negocioID)
                .child(sucursalID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            firebaseCallback.enExito(snapshot,GET_SUCURSAL_ACCTION);
                        }else{
                            firebaseCallback.enFallo(null);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public static void getProductos(String sucursalID,FirebaseCallback<DataSnapshot> firebaseCallback){
        firebaseCallback.enInicio();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_PRODUCTOS)
                .child(sucursalID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            firebaseCallback.enExito(snapshot,GET_PRODUCTOS_ACCTION);
                        }else{
                            firebaseCallback.enFallo(new Exception("Sin productos"));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public static void guardaSucursal(SucursalModelo sucursalModelo, FirebaseCallback<Void> firebaseCallback) {
       firebaseCallback.enInicio();
       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
       databaseReference
          .child(Constantes.NODO_SUCURSAL)
          .child(sucursalModelo.getNegocioID())
          .child(sucursalModelo.getSucursalID())
          .setValue(sucursalModelo)
       .addOnSuccessListener(aVoid -> {
          firebaseCallback.enExito(null, 0);
       })
       .addOnFailureListener(exception -> {
          firebaseCallback.enFallo(exception);
       });
    }

    public static void insertPedido(PedidoModelo pedidoModelo,
                                    FirebaseCallback<DataSnapshot> firebaseCallback){
        firebaseCallback.enInicio();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_PEDIDOS)
                .child(pedidoModelo.getPedidoID())
                .setValue(pedidoModelo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseCallback.enExito(null,INSERT_PEDIDO_ACCTION);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebaseCallback.enFallo(e);
            }
        });
    }
    public static void updatePedido(PedidoModelo pedidoModelo,
                                    FirebaseCallback<DataSnapshot> firebaseCallback){
        firebaseCallback.enInicio();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_PEDIDOS)
                .child(pedidoModelo.getPedidoID())
                .setValue(pedidoModelo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseCallback.enExito(null,UPDATE_PEDIDO_ACCTION);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebaseCallback.enFallo(e);
            }
        });
    }
    public  static void insertProductosPedido(ArrayList<ProductosPedidoModelo> lista,
                                              String pedidoID,
                                              FirebaseCallback<DataSnapshot> firebaseCallback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_PRODUCTOS_DE_PEDIDOS)
                .child(pedidoID)
                .setValue(lista).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseCallback.enExito(null,INSERT_PRODUCTOS_PEDIDO_ACCTION);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebaseCallback.enFallo(e);
            }
        });
    }

    public  static void updateProductos(ArrayList<ProductoModelo> lista,
                                              String sucursalID,
                                              FirebaseCallback<DataSnapshot> firebaseCallback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_PRODUCTOS)
                .child(sucursalID)
                .setValue(lista).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseCallback.enExito(null,UPDATE_PRODUCTOS_ACCTION);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebaseCallback.enFallo(e);
            }
        });
    }
    public static void deletePedido(String pedidoId,
                                    FirebaseCallback<DataSnapshot> firebaseCallback){
        firebaseCallback.enInicio();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_PEDIDOS)
                .child(pedidoId)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseCallback.enExito(null,DELETE_PEDIDO_ACCTION);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebaseCallback.enFallo(e);
            }
        });
    }
    public  static void deleteProductosPedido(String pedidoID,
                                              FirebaseCallback<DataSnapshot> firebaseCallback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_PRODUCTOS_DE_PEDIDOS)
                .child(pedidoID)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseCallback.enExito(null,DELETE_PRODUCTOS_PEDIDO_ACCTION);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebaseCallback.enFallo(e);
            }
        });
    }
    public static void getSucursales(String negocioID, FirebaseCallback<DataSnapshot> firebaseCallback){
        firebaseCallback.enInicio();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constantes.NODO_SUCURSAL)
                .child(negocioID)
                .orderByKey()
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            firebaseCallback.enExito(snapshot,GET_SUCURSALES_ACCTION);
                        }else{
                            firebaseCallback.enFallo(null);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public static void deleteSucursal(String NegocioID,String sucursalID,
                                    FirebaseCallback<DataSnapshot> firebaseCallback){
        firebaseCallback.enInicio();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_SUCURSAL)
                .child(NegocioID)
                .child(sucursalID)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseCallback.enExito(null,DELETE_PEDIDO_ACCTION);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebaseCallback.enFallo(e);
            }
        });
    }

    public static void deleteProductos(String sucursalID, FirebaseCallback<DataSnapshot> firebaseCallback) {
        firebaseCallback.enInicio();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_PEDIDOS)
                .child(sucursalID)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseCallback.enExito(null,DELETE_PRODUCTOS_ACCTION);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebaseCallback.enFallo(e);
            }
        });
    }



    public static void guardaProducto(ProductoModelo productoModelo, FirebaseCallback<Void> firebaseCallback) {
        firebaseCallback.enInicio();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Constantes.NODO_PRODUCTOS)
                .child(productoModelo.getSucursalId())
                .child(productoModelo.getProductoId())
                .setValue(productoModelo)
                .addOnSuccessListener(aVoid -> {
                    firebaseCallback.enExito(null, 0);
                })
                .addOnFailureListener(exception -> {
                    firebaseCallback.enFallo(exception);
                });
    }

}
