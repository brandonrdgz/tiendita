package com.example.tiendita.ui.pedidos;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFirebaseAuth;
import com.example.tiendita.datos.firebase.AccionesFirebaseRTDataBase;
import com.example.tiendita.datos.firebase.FirebaseCallback;
import com.example.tiendita.datos.modelos.PedidoModelo;
import com.example.tiendita.utilidades.Constantes;
import com.example.tiendita.utilidades.Dialogo;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class PedidosFragment extends Fragment implements FirebaseCallback<DataSnapshot>,
        AdapterView.OnItemClickListener

{

    private PedidosViewModel mViewModel;
    private ListView listView;
    private TextView tvSinPedidos;
    private Boolean esNegocio;
    private ArrayList<PedidoModelo> listaPedidos;
    private AlertDialog alertDialog;

    public static PedidosFragment newInstance() {
        return new PedidosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pedidos, container, false);
        Bundle data = this.getArguments();
        if (data != null) {
            esNegocio=data.getBoolean(Constantes.CONST_NEGOCIO_TYPE);
        }else{
            esNegocio=false;
        }
        initComps(root);
        return root;
    }

    private void initComps(View root) {
        listaPedidos=new ArrayList<>();
        listView=root.findViewById(R.id.lv_pedidos);
        tvSinPedidos=root.findViewById(R.id.tv_sin_pedidos_label);

        if(esNegocio) {
            AccionesFirebaseRTDataBase.getListaPedidos(AccionesFirebaseAuth.getUID(),
                    esNegocio,
                    PedidosFragment.this);
        }else{
            AccionesFirebaseRTDataBase.getListaPedidos(AccionesFirebaseAuth.getUID(),
                    esNegocio,
                    PedidosFragment.this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PedidosViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void enInicio() {
        alertDialog = Dialogo.dialogoProceso(getContext(), R.string.msj_cargando_lista_pedidos);
        Dialogo.muestraDialogoProceso(alertDialog);
    }

    @Override
    public void enExito(DataSnapshot respuesta, int accion) {
        for (DataSnapshot dataSnapshot : respuesta.getChildren()) {
            HashMap pedidoHash = (HashMap) dataSnapshot.getValue();
            PedidoModelo pedido= new PedidoModelo();
            pedido.setClienteID(pedidoHash.get(Constantes.CONST_PEDIDO_CLIENTE_ID).toString());
            pedido.setFecha(pedidoHash.get(Constantes.CONST_PEDIDO_FECHA).toString());
            pedido.setHora(pedidoHash.get(Constantes.CONST_PEDIDO_HORA).toString());
            pedido.setNegocioID(pedidoHash.get(Constantes.CONST_PEDIDO_NEGOCIO_ID).toString());
            pedido.setPago(Float.parseFloat(pedidoHash.get(Constantes.CONST_PEDIDO_PAGO).toString()));
            pedido.setPedidoID(pedidoHash.get(Constantes.CONST_PEDIDO_ID).toString());
            pedido.setSucursalID(pedidoHash.get(Constantes.CONST_PEDIDO_SUCURSAL_ID).toString());
            pedido.setTotalProductos(Integer.parseInt(pedidoHash.get(Constantes.CONST_PEDIDO_TOTAL_PROD).toString()));
            listaPedidos.add(pedido);
        }
        ArrayAdapter<PedidoModelo> adapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1,
                listaPedidos);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
        tvSinPedidos.setVisibility(View.GONE);

        Dialogo.ocultaDialogoProceso(alertDialog);
    }



    @Override
    public void enFallo(Exception excepcion) {
        Dialogo.ocultaDialogoProceso(alertDialog);
        Toast.makeText(this.getContext(), R.string.sin_pedidos, Toast.LENGTH_LONG).show();
        listView.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //redirect vista de pedido
                    Bundle data = new Bundle();
                    data.putString(Constantes.CONST_PEDIDO_ID,listaPedidos.get(position).getPedidoID());
                    data.putString(Constantes.CONST_PEDIDO_NEGOCIO_ID,listaPedidos.get(position).getNegocioID());
                    data.putString(Constantes.CONST_PEDIDO_SUCURSAL_ID,listaPedidos.get(position).getSucursalID());
                    data.putString(Constantes.CONST_PEDIDO_CLIENTE_ID,listaPedidos.get(position).getClienteID());
                    data.putString(Constantes.CONST_PEDIDO_FECHA,listaPedidos.get(position).getFecha());
                    data.putString(Constantes.CONST_PEDIDO_HORA,listaPedidos.get(position).getHora());
                    data.putFloat(Constantes.CONST_PEDIDO_PAGO,listaPedidos.get(position).getPago());
                    data.putInt(Constantes.CONST_PEDIDO_TOTAL_PROD,listaPedidos.get(position).getTotalProductos());

                    if(esNegocio){
                        NavHostFragment.findNavController(this)
                                .navigate(R.id.action_nav_pedidosn_to_nav_pedidon, data);
                    }else{
                    NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_nav_pedidosu_to_nav_pedidou, data);
                    }

    }
}