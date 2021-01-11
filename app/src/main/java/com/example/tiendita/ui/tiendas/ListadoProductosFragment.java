package com.example.tiendita.ui.tiendas;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFirebaseAuth;
import com.example.tiendita.datos.firebase.AccionesFirebaseRTDataBase;
import com.example.tiendita.datos.firebase.FirebaseCallback;
import com.example.tiendita.datos.modelos.ProductoModelo;
import com.example.tiendita.datos.modelos.SucursalModelo;
import com.example.tiendita.utilidades.Constantes;
import com.example.tiendita.utilidades.Dialogo;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class ListadoProductosFragment extends Fragment implements FirebaseCallback<DataSnapshot>,
        AdapterView.OnItemClickListener,
        View.OnClickListener  {

    private ListadoProductosViewModel mViewModel;
    private ListView listView;
    private TextView textView;
    private Button button;
    private ArrayList<ProductoModelo> listaProductos;
    private AlertDialog alertDialog;

    private boolean esNegocio;
    private String nombreSucursal, idNegocio, idSucursal;

    public static ListadoProductosFragment newInstance() {
        return new ListadoProductosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_listado_productos, container, false);
        recuperaDatosSucursal(this.getArguments(),root);
        return root;
    }

    private void recuperaDatosSucursal(Bundle datos,View root) {
        if (datos != null) {
            esNegocio = datos.getBoolean(Constantes.CONST_NEGOCIO_TYPE);
            nombreSucursal = datos.getString("nombreSucursal");
            idNegocio = datos.getString("idNegocio");
            idSucursal = datos.getString("idSucursal");
            initComp(root);
        }
    }

    private void initComp(View root){
        listView = root.findViewById(R.id.lv_productos);
        textView = root.findViewById(R.id.tv_sin_productos_label);
        button = root.findViewById(R.id.bttn_agregar_producto_sucursal);

        button.setOnClickListener(this::onClick);

        AccionesFirebaseRTDataBase.getProductos(idSucursal,this);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ListadoProductosViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void enInicio() {
        alertDialog = Dialogo.dialogoProceso(getContext(), R.string.msj_cargando_lista_sucursales);
        Dialogo.muestraDialogoProceso(alertDialog);
    }

    @Override
    public void enExito(DataSnapshot respuesta, int accion) {
        listaProductos=new ArrayList<>();
        for (DataSnapshot dataSnapshot : respuesta.getChildren()) {
            ProductoModelo productoModelo = new ProductoModelo();
            productoModelo.setNombreProducto(dataSnapshot.child(Constantes.CONST_PRODUCTO_NOMBRE).getValue().toString());
            productoModelo.setCantidad(Integer.valueOf(dataSnapshot.child(Constantes.CONST_PRODUCTO_CANTIDAD).getValue().toString()));
            productoModelo.setDescripcion(dataSnapshot.child(Constantes.CONST_PRODUCTO_DESCRIPCION).getValue().toString());
            productoModelo.setPrecio(Float.valueOf(dataSnapshot.child(Constantes.CONST_PRODUCTO_PRECIO).getValue().toString()));
            listaProductos.add(productoModelo);
        }
        ArrayAdapter<ProductoModelo> adapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1,
                listaProductos);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        Dialogo.ocultaDialogoProceso(alertDialog);
    }

    @Override
    public void enFallo(Exception excepcion) {
        Dialogo.ocultaDialogoProceso(alertDialog);
        Toast.makeText(this.getContext(), R.string.sin_productos_encontrados, Toast.LENGTH_LONG).show();
        textView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle data = new Bundle();
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_sucursales_to_nav_detalle_sucursaln, data);
    }

    @Override
    public void onClick(View v) {
        Bundle data = new Bundle();
        data.putBoolean(Constantes.CONST_NUEVA_TYPE, true);
        data.putString("nombreSucursal",nombreSucursal);
        data.putString("idSucursal",idSucursal);
        data.putString("idNegocio",idNegocio);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_listado_producto_to_nav_editar_productos, data);
    }
}