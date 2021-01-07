package com.example.tiendita.ui.pedidos;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tiendita.R;
import com.example.tiendita.utilidades.Constantes;

public class EditarPedidoFragment extends Fragment {
    private ImageView ivBuscar,ivProducto;
    private EditText tfBusqueda;
    private ListView lvDisponible,lvPedido;
    private TextView tvNombre,tvDescripcion,tvPrecio,tvDisponible;
    private Spinner spCantidad;
    private Button bttnAgregar,bttnAceptar,bttnDescartar;
    private String pedidoId,sucursalId;
    private Boolean esEdicion;

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
        spCantidad=root.findViewById(R.id.sp_cantidad_producto_pedido);
        bttnAceptar=root.findViewById(R.id.bttn_guardar_producto_pedido);
        bttnDescartar=root.findViewById(R.id.bttn_descartar_producto_pedido);
        bttnAgregar=root.findViewById(R.id.bttn_agregar_producto_pedido);




    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditarPedidoViewModel.class);
        // TODO: Use the ViewModel
    }

}