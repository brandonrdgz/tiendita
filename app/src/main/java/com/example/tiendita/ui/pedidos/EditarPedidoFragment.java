package com.example.tiendita.ui.pedidos;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFireStorage;
import com.example.tiendita.datos.firebase.AccionesFirebaseAuth;
import com.example.tiendita.datos.firebase.AccionesFirebaseRTDataBase;
import com.example.tiendita.datos.firebase.DownloadCallback;
import com.example.tiendita.datos.firebase.FirebaseCallback;
import com.example.tiendita.datos.modelos.PedidoModelo;
import com.example.tiendita.datos.modelos.ProductoModelo;
import com.example.tiendita.datos.modelos.ProductosPedidoModelo;
import com.example.tiendita.datos.modelos.SucursalModelo;
import com.example.tiendita.ui.home.HomeFragment;
import com.example.tiendita.utilidades.Constantes;
import com.example.tiendita.utilidades.Dialogo;
import com.example.tiendita.utilidades.ImageManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class EditarPedidoFragment extends Fragment implements View.OnClickListener,
        FirebaseCallback<DataSnapshot>,
        DownloadCallback<Task<FileDownloadTask.TaskSnapshot>>,
        DialogInterface.OnClickListener
        {
    private ImageView ivBuscar,ivProducto;
    private EditText tfBusqueda;
            private EditText tfCantidad;
    private ListView lvDisponible,lvPedido;
    private TextView tvNombre,tvDescripcion,tvPrecio,tvDisponible,tvCantidad,tvSinProductos,tvProductos,tvPedido;
    private Button bttnAgregaProd, bttnGuardarPedido, bttnCancelarEdicionPedido, bttnCancelarProducto, bttnQuitarProducto;
    private String sucursalId;
    private Boolean esEdicion, esAdicion,seHaEliminado;
    private PedidoModelo currentPedido;
    private ArrayList<ProductoModelo> listaProductos;
    private ArrayList<ProductoModelo> listaProductosTotales;
    private ArrayList<ProductosPedidoModelo> listaPedido;
    private androidx.appcompat.app.AlertDialog alertDialog;

    private ProductoModelo currentProd;
    private ProductosPedidoModelo currentProdPedido;
    private SucursalModelo sucursal;

    private EditarPedidoViewModel mViewModel;

    public static EditarPedidoFragment newInstance() {
        return new EditarPedidoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_editar_pedido, container, false);
        Bundle data = this.getArguments();
        if (data != null) {
            esEdicion=data.getBoolean(Constantes.CONST_EDICION_TYPE);
            if(esEdicion) {
                currentPedido=new PedidoModelo();
                currentPedido.setClienteID(data.getString(Constantes.CONST_PEDIDO_CLIENTE_ID));
                currentPedido.setFecha(data.getString(Constantes.CONST_PEDIDO_FECHA));
                currentPedido.setHora(data.getString(Constantes.CONST_PEDIDO_HORA));
                currentPedido.setNegocioID(data.getString(Constantes.CONST_PEDIDO_NEGOCIO_ID));
                currentPedido.setPago(data.getFloat(Constantes.CONST_PEDIDO_PAGO));
                currentPedido.setPedidoID(data.getString(Constantes.CONST_PEDIDO_ID));
                currentPedido.setSucursalID(data.getString(Constantes.CONST_PEDIDO_SUCURSAL_ID));
                currentPedido.setTotalProductos(data.getInt(Constantes.CONST_PEDIDO_TOTAL_PROD));
            }
            sucursalId=data.getString(Constantes.CONST_SUCURSAL_ID);
            sucursal = data.getParcelable(Constantes.LLAVE_SUCURSAL);
            init(root);
        }
        return root;
    }

    private void init(View root) {
        esAdicion=true;
        seHaEliminado=false;
        ivBuscar=root.findViewById(R.id.ib_busca_editar_producto);
        ivProducto=root.findViewById(R.id.iv_foto_editar_producto);
        tfBusqueda=root.findViewById(R.id.tf_busca_editar_producto);
        lvDisponible=root.findViewById(R.id.lv_disponible);
        lvPedido=root.findViewById(R.id.lv_pedido);
        tvNombre=root.findViewById(R.id.tv_nombre_producto_pedido);
        tvDescripcion=root.findViewById(R.id.tv_descripcion_producto_pedido);
        tvPrecio=root.findViewById(R.id.tv_precio_producto_pedido);
        tvDisponible=root.findViewById(R.id.tv_disponible_producto_pedido);
        tvCantidad=root.findViewById(R.id.tv_cantidad_label);
        tvSinProductos=root.findViewById(R.id.tv_sin_productos_label);
        tvProductos=root.findViewById(R.id.tv_disponibles_productos_label);
        tvPedido=root.findViewById(R.id.tv_pedido_productos_label);
        tfCantidad=root.findViewById(R.id.tf_cantidad_editar_producto);
        bttnGuardarPedido =root.findViewById(R.id.bttn_guardar_pedido_editar);
        bttnCancelarEdicionPedido =root.findViewById(R.id.bttn_cancelar_pedido_editar);
        bttnAgregaProd=root.findViewById(R.id.bttn_agregar_producto_pedido);
        bttnCancelarProducto =root.findViewById(R.id.bttn_cancelar_producto_pedido);
        bttnQuitarProducto =root.findViewById(R.id.bttn_quitar_producto_pedido);

        hidePanel(true);

        bttnGuardarPedido.setOnClickListener(this);
        bttnCancelarEdicionPedido.setOnClickListener(this);
        bttnAgregaProd.setOnClickListener(this);
        bttnCancelarProducto.setOnClickListener(this);
        bttnQuitarProducto.setOnClickListener(this);
        ivBuscar.setOnClickListener(this);

        tvSinProductos.setVisibility(View.GONE);

        listaPedido=new ArrayList<>();
        listaProductos=new ArrayList<>();
        listaProductosTotales=new ArrayList<>();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if(esEdicion) {
                    Bundle data = new Bundle();

                    data.putBoolean(Constantes.CONST_EDICION_TYPE, true);
                    data.putString(Constantes.CONST_PEDIDO_ID, currentPedido.getPedidoID());
                    data.putString(Constantes.CONST_PEDIDO_NEGOCIO_ID, currentPedido.getNegocioID());
                    data.putString(Constantes.CONST_PEDIDO_SUCURSAL_ID, currentPedido.getSucursalID());
                    data.putString(Constantes.CONST_PEDIDO_CLIENTE_ID, currentPedido.getClienteID());
                    data.putString(Constantes.CONST_PEDIDO_FECHA, currentPedido.getFecha());
                    data.putString(Constantes.CONST_PEDIDO_HORA, currentPedido.getHora());
                    data.putFloat(Constantes.CONST_PEDIDO_PAGO, currentPedido.getPago());
                    data.putInt(Constantes.CONST_PEDIDO_TOTAL_PROD, currentPedido.getTotalProductos());

                    NavHostFragment.findNavController(EditarPedidoFragment.this)
                            .navigate(R.id.action_nav_editpedido_to_nav_pedidou, data);
                }else{
                    Bundle data = new Bundle();
                    data.putBoolean(Constantes.CONST_EDICION_TYPE, false);
                    data.putString(Constantes.CONST_SUCURSAL_ID, sucursal.getSucursalID());
                    data.putParcelable(Constantes.LLAVE_SUCURSAL, sucursal);
                    NavHostFragment.findNavController(EditarPedidoFragment.this)
                            .navigate(R.id.action_nav_editpedido_to_nav_detalle_sucursalu, data);

                }

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        AccionesFirebaseRTDataBase.getProductos(sucursalId,this);
    }
    private void hidePanel(boolean ban){
        if(ban){
            ivProducto.setVisibility(View.GONE);
            tvNombre.setVisibility(View.GONE);
            tvPrecio.setVisibility(View.GONE);
            tvDescripcion.setVisibility(View.GONE);
            tvDisponible.setVisibility(View.GONE);
            tvCantidad.setVisibility(View.GONE);
            tfCantidad.setVisibility(View.GONE);
            bttnAgregaProd.setVisibility(View.GONE);
            bttnCancelarProducto.setVisibility(View.GONE);
            bttnQuitarProducto.setVisibility(View.GONE);
        }else{
            ivProducto.setVisibility(View.VISIBLE);
            tvNombre.setVisibility(View.VISIBLE);
            tvPrecio.setVisibility(View.VISIBLE);
            tvDescripcion.setVisibility(View.VISIBLE);
            tvDisponible.setVisibility(View.VISIBLE);
            tvCantidad.setVisibility(View.VISIBLE);
            tfCantidad.setVisibility(View.VISIBLE);
            bttnAgregaProd.setVisibility(View.VISIBLE);
            bttnCancelarProducto.setVisibility(View.VISIBLE);
            bttnQuitarProducto.setVisibility(View.VISIBLE);

        }
        
    }
            private void AddPanel(){
                    ivProducto.setVisibility(View.VISIBLE);
                    tvNombre.setVisibility(View.VISIBLE);
                    tvPrecio.setVisibility(View.VISIBLE);
                    tvDescripcion.setVisibility(View.VISIBLE);
                    tvDisponible.setVisibility(View.VISIBLE);
                    tvCantidad.setVisibility(View.VISIBLE);
                    tfCantidad.setVisibility(View.VISIBLE);
                    bttnAgregaProd.setVisibility(View.VISIBLE);
                    bttnCancelarProducto.setVisibility(View.VISIBLE);



            }
            private void EditPanel(){
                    ivProducto.setVisibility(View.VISIBLE);
                    tvNombre.setVisibility(View.VISIBLE);
                    tvPrecio.setVisibility(View.VISIBLE);
                    tvDescripcion.setVisibility(View.VISIBLE);
                    tvDisponible.setVisibility(View.VISIBLE);
                    tvCantidad.setVisibility(View.VISIBLE);
                    tfCantidad.setVisibility(View.VISIBLE);
                    bttnAgregaProd.setVisibility(View.VISIBLE);
                    bttnQuitarProducto.setVisibility(View.VISIBLE);
                bttnCancelarProducto.setVisibility(View.VISIBLE);

            }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditarPedidoViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bttn_guardar_pedido_editar:{
                if(listaPedido.isEmpty()){
                    Toast.makeText(this.getContext(), R.string.pedido_vacio, Toast.LENGTH_LONG).show();
                }else {
                    if (esEdicion) {
                        AccionesFirebaseRTDataBase.updatePedido(currentPedido, this);
                    } else {
                        //guardado del nuevo pedido
                        currentPedido = new PedidoModelo();
                        currentPedido.setTotalProductos(getTotalProductos());
                        currentPedido.setSucursalID(sucursalId);
                        currentPedido.setPago(getTotalPagar());
                        currentPedido.setHora(getHora());
                        currentPedido.setFecha(getFecha());
                        currentPedido.setNegocioID(sucursal.getNegocioID());
                        currentPedido.setClienteID(AccionesFirebaseAuth.getUID());
                        AccionesFirebaseRTDataBase.insertPedido(currentPedido, this);
                    }
                }

            }
                break;
            case R.id.bttn_cancelar_pedido_editar: {
                if(esEdicion){
                    //eliminacion del pedido
                    //Requiere confirmacion
                    AlertDialog.Builder dialogo= new AlertDialog.Builder(getContext());
                    dialogo.setTitle(R.string.header_eliminar);
                    dialogo.setMessage(R.string.message_eliminar_pedido);
                    dialogo.setPositiveButton(R.string.action_eliminar, this);
                    dialogo.setNegativeButton(R.string.action_cancelar,this);
                    dialogo.show();
                    //REDIRECT VISTA PEDIDOS

                }else{
                    //descarte del nuevo pedido
                    //redirect a vista de sucursal
                    Bundle data = new Bundle();
                    data.putBoolean(Constantes.CONST_EDICION_TYPE, false);
                    data.putString(Constantes.CONST_SUCURSAL_ID, sucursal.getSucursalID());
                    data.putParcelable(Constantes.LLAVE_SUCURSAL, sucursal);
                    NavHostFragment.findNavController(EditarPedidoFragment.this)
                            .navigate(R.id.action_nav_editpedido_to_nav_detalle_sucursalu, data);

                }

            }
            break;
            case R.id.bttn_agregar_producto_pedido:{
                    if (esAdicion) {
                        //adicion del nuevo producto a la lista del pedido
                        if(Integer.parseInt(tfCantidad.getText().toString())<=currentProd.getCantidad()) {
                            ProductosPedidoModelo productosPedidoModelo = new ProductosPedidoModelo();
                            productosPedidoModelo.setCantidad(Integer.parseInt(tfCantidad.getText().toString()));
                            productosPedidoModelo.setDescripcion(currentProd.getDescripcion());
                            productosPedidoModelo.setNombreProducto(currentProd.getNombreProducto());
                            if (esEdicion) {
                                productosPedidoModelo.setPedidoID(currentPedido.getPedidoID());
                            }
                            productosPedidoModelo.setPrecio(currentProd.getPrecio());
                            productosPedidoModelo.setRemoteImg(currentProd.getRemoteImg());
                            productosPedidoModelo.setProductoId(currentProd.getProductoId());
                            productosPedidoModelo.setSucursalId(currentProd.getSucursalId());
                            productosPedidoModelo.setNegocioId(currentProd.getNegocioId());
                            listaPedido.add(productosPedidoModelo);
                            int diferencia = currentProd.getCantidad() - productosPedidoModelo.getCantidad();
                            currentProd.setCantidad(diferencia);
                            actualizaListas(0);
                            hidePanel(true);
                        }else{
                            Toast.makeText(this.getContext(), R.string.error_exceso,Toast.LENGTH_LONG).show();

                        }
                    } else {
                        //modificacion de la cantidad del producto en la lista del pedido



                            int prodIndex =-1;
                            if(currentProd==null){
                                prodIndex = getIndex(true, currentProdPedido.getProductoId());
                            }else if (currentProdPedido==null){
                                prodIndex = getIndex(false, currentProd.getProductoId());
                            }

                            if (prodIndex < 0) {
                                Toast.makeText(this.getContext(), R.string.error_cargar_datos, Toast.LENGTH_LONG).show();
                            } else {
                                if(currentProd==null){
                                    currentProd=listaProductos.get(prodIndex);
                                }else if (currentProdPedido==null){
                                    currentProdPedido=listaPedido.get(prodIndex);
                                }
                                if (Integer.parseInt(tfCantidad.getText().toString()) != currentProd.getCantidad()) {
                                if(Integer.parseInt(tfCantidad.getText().toString())<=currentProd.getCantidad()) {
                               // int suma = listaProductos.get(prodIndex).getCantidad() + currentProdPedido.getCantidad();
                                    //
                                    int total=currentProd.getCantidad()+currentProdPedido.getCantidad();
                                    int diferencia = total-Integer.parseInt(tfCantidad.getText().toString());
                                currentProdPedido.setCantidad(Integer.parseInt(tfCantidad.getText().toString()));
                                currentProd.setCantidad(diferencia);
                                actualizaListas(0);
                                    hidePanel(true);
                                }else{
                                    Toast.makeText(this.getContext(), R.string.error_exceso,Toast.LENGTH_LONG).show();

                                }

                        }
                    }
                }

            }
                break;
            case R.id.bttn_cancelar_producto_pedido:{
                actualizaListas(0);
                hidePanel(true);
            }
            break;
            case R.id.ib_busca_editar_producto:{
                String busqueda=tfBusqueda.getText().toString();
                if(!busqueda.trim().isEmpty()) {
                    if (busqueda.equals("TODO")) {
                        listaProductos = listaProductosTotales;
                    } else {
                        listaProductos = new ArrayList<>();
                        for (ProductoModelo productoModelo : listaProductosTotales) {
                            if (productoModelo.getNombreProducto().contains(busqueda)) {
                                listaProductos.add(productoModelo);
                            }
                        }
                    }
                    if(listaProductos.isEmpty()){
                        tvSinProductos.setText(R.string.sin_productos_encontrados);
                        tvSinProductos.setVisibility(View.VISIBLE);
                        lvDisponible.setVisibility(View.GONE);
                    }else {
                        tvSinProductos.setVisibility(View.GONE);
                        lvDisponible.setVisibility(View.VISIBLE);
                        actualizaListas(1);
                    }
                }




            }
                break;
            case R.id.bttn_quitar_producto_pedido:{
                int prodIndex =-1;
                if(currentProd==null){
                    prodIndex = getIndex(true, currentProdPedido.getProductoId());
                }else if (currentProdPedido==null){
                    prodIndex = getIndex(false, currentProd.getProductoId());
                }
                    if (prodIndex<0) {
                        Toast.makeText(this.getContext(), R.string.error_cargar_datos, Toast.LENGTH_LONG).show();
                    } else {
                        if(currentProd==null){
                            currentProd=listaProductos.get(prodIndex);
                        }else if (currentProdPedido==null){
                            currentProdPedido=listaPedido.get(prodIndex);
                        }
                        int suma = currentProd.getCantidad() + currentProdPedido.getCantidad();
                        currentProd.setCantidad(suma);
                        listaPedido.remove(currentProdPedido);
                        hidePanel(true);
                        actualizaListas(0);
                    }
            }
                break;
        }
    }

    private void restock() {
        int suma,indx;
        for(ProductosPedidoModelo productosPedidoModelo:listaPedido){
            listaProductos=listaProductosTotales;
            indx=getIndex(true,productosPedidoModelo.getProductoId());
            suma=productosPedidoModelo.getCantidad()+listaProductosTotales.get(indx).getCantidad();
            listaProductosTotales.get(indx).setCantidad(suma);
        }
    }

    private String getFecha() {
        Calendar calendar=Calendar.getInstance();
        int anio=calendar.get(Calendar.YEAR);
        int mes=calendar.get(Calendar.MONTH);
        int dia=calendar.get(Calendar.DAY_OF_MONTH);
        String diaf=(dia<10)?"0"+(dia):String.valueOf(dia);
        String mesf=(mes+1<10)?"0"+(mes+1):String.valueOf(mes+1);
        return diaf+"/"+mesf+"/"+anio;
    }

    private String getHora() {
        Calendar calendar=Calendar.getInstance();
        int hora =calendar.get(Calendar.HOUR_OF_DAY);
        int minutos = calendar.get(Calendar.MINUTE);
        return  hora+":"+minutos;
    }

    private float getTotalPagar() {
        float total=0;
        for(ProductosPedidoModelo productosPedidoModelo:listaPedido){
            total+=productosPedidoModelo.getCantidad()*productosPedidoModelo.getPrecio();
        }
        return  total;
    }
            private int getTotalProductos() {
                int total=0;
                for(ProductosPedidoModelo productosPedidoModelo:listaPedido){
                    total+=productosPedidoModelo.getCantidad();
                }
                return  total;
            }


            @Override
    public void enInicio() {
        alertDialog = Dialogo.dialogoProceso(getContext(), R.string.msj_operacion_datos);
        Dialogo.muestraDialogoProceso(alertDialog);

    }


    @Override
    public void enExito(DataSnapshot respuesta, int accion) {
        Dialogo.ocultaDialogoProceso(alertDialog);
        switch (accion) {
            case AccionesFirebaseRTDataBase.GET_PRODUCTOS_ACCTION: {
                for (DataSnapshot dataSnapshot : respuesta.getChildren()) {
                    HashMap pedidoHash = (HashMap) dataSnapshot.getValue();
                    ProductoModelo productoModelo=new ProductoModelo();
                    productoModelo.setNombreProducto(pedidoHash.get(Constantes.CONST_PRODUCTO_NOMBRE).toString());
                    productoModelo.setCantidad(Integer.parseInt(dataSnapshot.child(Constantes.CONST_PRODUCTO_CANTIDAD).getValue().toString()));
                    productoModelo.setDescripcion(dataSnapshot.child(Constantes.CONST_PRODUCTO_DESCRIPCION).getValue().toString());
                    productoModelo.setNombreProducto(dataSnapshot.child(Constantes.CONST_PRODUCTO_NOMBRE).getValue().toString());
                    productoModelo.setPrecio(Float.parseFloat(dataSnapshot.child(Constantes.CONST_PRODUCTO_PRECIO).getValue().toString()));
                    productoModelo.setRemoteImg(dataSnapshot.child(Constantes.CONST_BASE_REMOTEIMG).getValue().toString());
                    productoModelo.setProductoId(dataSnapshot.child(Constantes.CONST_PRODUCTO_ID).getValue().toString());
                    productoModelo.setSucursalId(dataSnapshot.child(Constantes.CONST_PRODUCTO_SUCURSAL_ID).getValue().toString());
                    productoModelo.setNegocioId(dataSnapshot.child(Constantes.CONST_PRODUCTO_NEGOCIO_ID).getValue().toString());
                    listaProductosTotales.add(productoModelo);
                    listaProductos.add(productoModelo);
                }
                ArrayAdapter<ProductoModelo> adapter = new ArrayAdapter<>(this.getContext(),
                        android.R.layout.simple_list_item_1,
                        listaProductos);
                lvDisponible.setAdapter(adapter);
                actualizaListas(1);
                if(esEdicion){
                    AccionesFirebaseRTDataBase.getListaProductosPedido(currentPedido.getPedidoID(),this);
                }
            }
            break;
            case AccionesFirebaseRTDataBase.GET_LISTA_PRODUCTOS_PEDIDO_ACCTION: {
                for (DataSnapshot dataSnapshot : respuesta.getChildren()) {
                    ProductosPedidoModelo productosPedidoModelo=new ProductosPedidoModelo();
                    productosPedidoModelo.setCantidad(Integer.parseInt(dataSnapshot.child(Constantes.CONST_PRODUCTO_CANTIDAD).getValue().toString()));
                    productosPedidoModelo.setDescripcion(dataSnapshot.child(Constantes.CONST_PRODUCTO_DESCRIPCION).getValue().toString());
                    productosPedidoModelo.setNombreProducto(dataSnapshot.child(Constantes.CONST_PRODUCTO_NOMBRE).getValue().toString());
                    productosPedidoModelo.setPedidoID(dataSnapshot.child(Constantes.CONST_PEDIDO_ID).getValue().toString());
                    productosPedidoModelo.setPrecio(Float.parseFloat(dataSnapshot.child(Constantes.CONST_PRODUCTO_PRECIO).getValue().toString()));
                    productosPedidoModelo.setRemoteImg(dataSnapshot.child(Constantes.CONST_BASE_REMOTEIMG).getValue().toString());
                    productosPedidoModelo.setProductoId(dataSnapshot.child(Constantes.CONST_PRODUCTO_ID).getValue().toString());
                    productosPedidoModelo.setSucursalId(dataSnapshot.child(Constantes.CONST_PRODUCTO_SUCURSAL_ID).getValue().toString());
                    productosPedidoModelo.setNegocioId(dataSnapshot.child(Constantes.CONST_PRODUCTO_NEGOCIO_ID).getValue().toString());

                    listaPedido.add(productosPedidoModelo);
                }
                ArrayAdapter<ProductosPedidoModelo> adapter = new ArrayAdapter<>(this.getContext(),
                        android.R.layout.simple_list_item_1,
                        listaPedido);
                lvPedido.setAdapter(adapter);
                actualizaListas(2);

            }
            break;
            case AccionesFirebaseRTDataBase.UPDATE_PEDIDO_ACCTION:
            case AccionesFirebaseRTDataBase.INSERT_PEDIDO_ACCTION: {
                AccionesFirebaseRTDataBase.insertProductosPedido(listaPedido,currentPedido.getPedidoID(),this);
            }
            break;
            case AccionesFirebaseRTDataBase.UPDATE_PRODUCTOS_ACCTION: {
                if(esEdicion){
                    if(seHaEliminado){
                        NavHostFragment.findNavController(this)
                                .navigate(R.id.action_nav_editpedido_to_nav_pedidosu);

                    }else {
                        Toast.makeText(this.getContext(), R.string.pedido_actualizado, Toast.LENGTH_LONG).show();
                        Bundle data = new Bundle();
                        data.putString(Constantes.CONST_PEDIDO_ID, currentPedido.getPedidoID());
                        data.putString(Constantes.CONST_PEDIDO_NEGOCIO_ID, currentPedido.getNegocioID());
                        data.putString(Constantes.CONST_PEDIDO_SUCURSAL_ID, currentPedido.getSucursalID());
                        data.putString(Constantes.CONST_PEDIDO_CLIENTE_ID, currentPedido.getClienteID());
                        data.putString(Constantes.CONST_PEDIDO_FECHA, currentPedido.getFecha());
                        data.putString(Constantes.CONST_PEDIDO_HORA, currentPedido.getHora());
                        data.putFloat(Constantes.CONST_PEDIDO_PAGO, currentPedido.getPago());
                        data.putInt(Constantes.CONST_PEDIDO_TOTAL_PROD, currentPedido.getTotalProductos());
                        NavHostFragment.findNavController(this)
                                .navigate(R.id.action_nav_editpedido_to_nav_pedidou, data);
                    }

                }else{
                    Toast.makeText(this.getContext(), R.string.pedido_guardado, Toast.LENGTH_LONG).show();
                    Bundle datos = new Bundle();
                    datos.putBoolean(Constantes.CONST_EDICION_TYPE, false);
                    datos.putString(Constantes.CONST_SUCURSAL_ID, sucursal.getSucursalID());
                    datos.putParcelable(Constantes.LLAVE_SUCURSAL, sucursal);
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_editpedido_to_nav_detalle_sucursalu,datos);
                }

            }
            break;
            case AccionesFirebaseRTDataBase.DELETE_PEDIDO_ACCTION: {
                AccionesFirebaseRTDataBase.deleteProductosPedido(currentPedido.getPedidoID(),this);
            }
            break;
            case AccionesFirebaseRTDataBase.INSERT_PRODUCTOS_PEDIDO_ACCTION:
            case AccionesFirebaseRTDataBase.DELETE_PRODUCTOS_PEDIDO_ACCTION: {
                AccionesFirebaseRTDataBase.updateProductos(listaProductosTotales,currentPedido.getSucursalID(),this);
            }
            break;
        }

    }

    @Override
    public void enFallo(Exception excepcion) {
        Dialogo.ocultaDialogoProceso(alertDialog);
        if(excepcion.getMessage().equals("Sin productos")) {
            tvSinProductos.setText(R.string.sin_productos);
            tvSinProductos.setVisibility(View.VISIBLE);
            tvPedido.setVisibility(View.GONE);
            tvProductos.setVisibility(View.GONE);
            lvDisponible.setVisibility(View.GONE);
            lvPedido.setVisibility(View.GONE);
            ivBuscar.setEnabled(false);
            tfBusqueda.setEnabled(false);
            bttnGuardarPedido.setVisibility(View.GONE);
            bttnCancelarEdicionPedido.setVisibility(View.GONE);
            Toast.makeText(this.getContext(), R.string.sin_productos, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this.getContext(), R.string.error_datos, Toast.LENGTH_LONG).show();
        }

    }


    private void showProduct(boolean esProductodeListaDisp) {
        if(esProductodeListaDisp) {
            int prodIndex=getIndex(false,currentProd.getProductoId());
            if(prodIndex<0){
                tvDisponible.setText(currentProd.getCantidad()+"");
                esAdicion=true;
                AddPanel();
                bttnAgregaProd.setText(R.string.agregar);
            }else {
                int suma=currentProd.getCantidad()+listaPedido.get(prodIndex).getCantidad();
                tvDisponible.setText(suma+"");
                esAdicion=false;
                EditPanel();
                tfCantidad.setText(listaPedido.get(prodIndex).getCantidad()+"");
                bttnAgregaProd.setText(R.string.guardarBtn);
            }
            String localRef=AccionesFirebaseRTDataBase.getLocalImgRef(currentProd.getProductoId(),getContext());
            ImageManager.loadImage(localRef,ivProducto,this.getContext());
            tvNombre.setText(currentProd.getNombreProducto());
            tvDescripcion.setText(currentProd.getDescripcion());
            tvPrecio.setText("$"+currentProd.getPrecio());

        }else{
            int prodIndex=getIndex(true,currentProdPedido.getProductoId());
            if(prodIndex<0){
                Toast.makeText(this.getContext(), R.string.error_cargar_datos, Toast.LENGTH_LONG).show();
            }else {
                int suma=listaProductos.get(prodIndex).getCantidad()+currentProdPedido.getCantidad();
                String localRef=AccionesFirebaseRTDataBase.getLocalImgRef(currentProdPedido.getProductoId(),getContext());
                ImageManager.loadImage(localRef,ivProducto,this.getContext());
                ivProducto.setVisibility(View.GONE);
                tvNombre.setText(currentProdPedido.getNombreProducto());
                tvDescripcion.setText(currentProdPedido.getDescripcion());
                tvPrecio.setText("$"+currentProdPedido.getPrecio());
                tvDisponible.setText(suma+"");
                tfCantidad.setText(currentProdPedido.getCantidad()+"");
                bttnAgregaProd.setText(R.string.guardarBtn);
                esAdicion=false;
                EditPanel();
            }

        }

    }

    private void actualizaListas(int op){
        currentProdPedido=null;
        currentProd=null;
        switch (op){
            case 0: {
                ArrayAdapter<ProductoModelo> adapter = new ArrayAdapter<>(this.getContext(),
                        android.R.layout.simple_list_item_1,
                        listaProductos);
                lvDisponible.setAdapter(adapter);
                lvDisponible.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        currentProd=listaProductos.get(position);
                        currentProdPedido=null;
                        String localRef=AccionesFirebaseRTDataBase.getLocalImgRef(currentProd.getProductoId(),getContext());
                        File filePhoto = new File(localRef);
                        if (filePhoto.exists()) {
                            showProduct(true);
                        } else {
                            AccionesFireStorage.downloadImg(currentProd.getRemoteImg(),
                                    getActivity(),
                                    getContext(),
                                    EditarPedidoFragment.this,
                                    currentProd.getProductoId());
                        }
                    }
                });
                ArrayAdapter<ProductosPedidoModelo> adapter2 = new ArrayAdapter<>(this.getContext(),
                        android.R.layout.simple_list_item_1,
                        listaPedido);
                lvPedido.setAdapter(adapter2);
                lvPedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        currentProdPedido=listaPedido.get(position);
                        currentProd=null;
                        String localRef=AccionesFirebaseRTDataBase.getLocalImgRef(currentProdPedido.getProductoId(),getContext());
                        File filePhoto = new File(localRef);
                        if (filePhoto.exists()) {
                            showProduct(false);
                        } else {
                            AccionesFireStorage.downloadImg(currentProdPedido.getRemoteImg(),
                                    getActivity(),
                                    getContext(),
                                    EditarPedidoFragment.this,
                                    currentProdPedido.getProductoId());
                        }
                    }
                });
            }break;
            case 1: {
                ArrayAdapter<ProductoModelo> adapter = new ArrayAdapter<>(this.getContext(),
                        android.R.layout.simple_list_item_1,
                        listaProductos);
                lvDisponible.setAdapter(adapter);
                lvDisponible.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        currentProd=listaProductos.get(position);
                        currentProdPedido=null;
                        String localRef=AccionesFirebaseRTDataBase.getLocalImgRef(currentProd.getProductoId(),getContext());
                        File filePhoto = new File(localRef);
                        if (filePhoto.exists()) {
                            showProduct(true);
                        } else {
                            AccionesFireStorage.downloadImg(currentProd.getRemoteImg(),
                                    getActivity(),
                                    getContext(),
                                    EditarPedidoFragment.this,
                                    currentProd.getProductoId());
                        }
                    }
                });
            }break;
            case 2: {
                ArrayAdapter<ProductosPedidoModelo> adapter2 = new ArrayAdapter<>(this.getContext(),
                        android.R.layout.simple_list_item_1,
                        listaPedido);
                lvPedido.setAdapter(adapter2);
                lvPedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        currentProdPedido=listaPedido.get(position);
                        currentProd=null;
                        String localRef=AccionesFirebaseRTDataBase.getLocalImgRef(currentProdPedido.getProductoId(),getContext());
                        File filePhoto = new File(localRef);
                        if (filePhoto.exists()) {
                            showProduct(false);
                        } else {
                            AccionesFireStorage.downloadImg(currentProdPedido.getRemoteImg(),
                                    getActivity(),
                                    getContext(),
                                    EditarPedidoFragment.this,
                                    currentProdPedido.getProductoId());
                        }
                    }
                });
            }break;
        }

    }

    private int getIndex(boolean deListDisponibles,String prodId){
        boolean notFound=true;
        int prodIndex=0;
        if(deListDisponibles) {
            while (notFound && (prodIndex < listaProductos.size())) {
                if (listaProductos.get(prodIndex).getProductoId().equals(prodId)) {
                    notFound = false;
                } else {
                    prodIndex++;
                }
            }
        }else{
            while(notFound && (prodIndex<listaPedido.size())) {
                if(listaPedido.get(prodIndex).getProductoId().equals(prodId)){
                    notFound=false;
                }else{
                    prodIndex++;
                }
            }

        }
        if(notFound){
            return -1;
        }else{
            return prodIndex;
        }
    }

    @Override
    public void enInicioDesc() {
        alertDialog = Dialogo.dialogoProceso(getContext(), R.string.msj_operacion_datos);
        Dialogo.muestraDialogoProceso(alertDialog);
    }

    @Override
    public void enExitoDesc(Task<FileDownloadTask.TaskSnapshot> respuesta, File localFile) {
        Dialogo.ocultaDialogoProceso(alertDialog);
        if(currentProd==null){
            showProduct(false);
        }else{
            showProduct(true);
        }

    }


    @Override
    public void enFalloDesc(Exception excepcion) {
        Dialogo.ocultaDialogoProceso(alertDialog);
        Toast.makeText(this.getContext(), R.string.error_cargar_img,Toast.LENGTH_LONG).show();
        Log.d("Descargar imagen","Error al descargar imagen\n Causa: "+excepcion.getCause());

    }
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case BUTTON_POSITIVE:
                        // int which = -1
                        restock();
                        seHaEliminado=true;
                        AccionesFirebaseRTDataBase.deletePedido(currentPedido.getPedidoID(),this);
                        dialog.dismiss();
                        break;
                }


            }
        }