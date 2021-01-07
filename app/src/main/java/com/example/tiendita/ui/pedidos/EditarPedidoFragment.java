package com.example.tiendita.ui.pedidos;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFirebaseRTDataBase;
import com.example.tiendita.datos.firebase.FirebaseCallback;
import com.example.tiendita.datos.modelos.PedidoModelo;
import com.example.tiendita.datos.modelos.ProductoModelo;
import com.example.tiendita.datos.modelos.ProductosPedidoModelo;
import com.example.tiendita.utilidades.Constantes;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class EditarPedidoFragment extends Fragment implements View.OnClickListener,
        FirebaseCallback<DataSnapshot> {
    private ImageView ivBuscar,ivProducto;
    private EditText tfBusqueda;
    private ListView lvDisponible,lvPedido;
    private TextView tvNombre,tvDescripcion,tvPrecio,tvDisponible,tvCantidad;
    private Spinner spCantidad; 
    private Button bttnAgregar,bttnAceptar,bttnDescartar,bttnCancelar;
    private String pedidoId,sucursalId;
    private Boolean esEdicion;
    private ArrayList<ProductoModelo> listaProductos;
    private ArrayList<ProductosPedidoModelo> listaPedidos;

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
                pedidoId = data.getString(Constantes.CONST_PEDIDO_ID);
            }
            sucursalId=data.getString(Constantes.CONST_SUCURSAL_ID);
            init(root);
        }
        return root;
    }

    private void init(View root) {
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
        spCantidad=root.findViewById(R.id.sp_cantidad_producto_pedido);
        bttnAceptar=root.findViewById(R.id.bttn_guardar_producto_pedido);
        bttnDescartar=root.findViewById(R.id.bttn_descartar_producto_pedido);
        bttnAgregar=root.findViewById(R.id.bttn_agregar_producto_pedido);
        bttnCancelar=root.findViewById(R.id.bttn_cancelar_producto_pedido);

        hidePanel(true);

        bttnAceptar.setOnClickListener(this);
        bttnDescartar.setOnClickListener(this);
        bttnAgregar.setOnClickListener(this);
        bttnCancelar.setOnClickListener(this);
        ivBuscar.setOnClickListener(this);

        //AccionesFirebaseRTDataBase.getProductos(sucursalId,this);
    }
    private void hidePanel(boolean ban){
        if(ban){
            ivProducto.setVisibility(View.GONE);
            lvDisponible.setVisibility(View.GONE);
            tvNombre.setVisibility(View.GONE);
            tvDescripcion.setVisibility(View.GONE);
            tvDisponible.setVisibility(View.GONE);
            spCantidad.setVisibility(View.GONE);
            tvCantidad.setVisibility(View.GONE);
            bttnAgregar.setVisibility(View.GONE);
            bttnCancelar.setVisibility(View.GONE);
        }else{
            ivProducto.setVisibility(View.VISIBLE);
            lvDisponible.setVisibility(View.VISIBLE);
            tvNombre.setVisibility(View.VISIBLE);
            tvDescripcion.setVisibility(View.VISIBLE);
            tvDisponible.setVisibility(View.VISIBLE);
            spCantidad.setVisibility(View.VISIBLE);
            tvCantidad.setVisibility(View.VISIBLE);
            bttnAgregar.setVisibility(View.VISIBLE);
            bttnCancelar.setVisibility(View.VISIBLE);

        }
        
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
            case R.id.bttn_guardar_producto_pedido:
                break;
            case R.id.bttn_descartar_producto_pedido:
            break;
            case R.id.bttn_agregar_producto_pedido:
                break;
            case R.id.bttn_cancelar_producto_pedido:
            break;
            case R.id.ib_busca_editar_producto:
                break;
        }
    }

    @Override
    public void enInicio() {

    }


    @Override
    public void enExito(DataSnapshot respuesta, int accion) {
        switch (accion) {
            case AccionesFirebaseRTDataBase.GET_PRODUCTOS_ACCTION: {
                for (DataSnapshot dataSnapshot : respuesta.getChildren()) {
                    HashMap pedidoHash = (HashMap) dataSnapshot.getValue();
                    ProductoModelo productoModelo=new ProductoModelo();
                    productoModelo.setNombreProducto(pedidoHash.get(Constantes.CONST_PRODUCTO_NOMBRE).toString());
                       listaProductos.add(productoModelo);
                }
                ArrayAdapter<ProductoModelo> adapter = new ArrayAdapter<>(this.getContext(),
                        android.R.layout.simple_list_item_1,
                        listaProductos);
                lvDisponible.setAdapter(adapter);
            }
            break;
        }

    }

    @Override
    public void enFallo(Exception excepcion) {

    }
}