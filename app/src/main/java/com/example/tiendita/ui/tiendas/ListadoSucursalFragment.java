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
import com.example.tiendita.datos.modelos.PedidoModelo;
import com.example.tiendita.datos.modelos.SucursalModelo;
import com.example.tiendita.utilidades.Constantes;
import com.example.tiendita.utilidades.Dialogo;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class ListadoSucursalFragment extends Fragment implements FirebaseCallback<DataSnapshot>,
        AdapterView.OnItemClickListener,
        View.OnClickListener {
    /*Muestra el listado de sucursales de un negocio
      Al elegir un item de la lista redirecciona a la vista detalles sucursal para visualizar o editar datos
      Al seleccionar la opcion nueva redirije a la vista detalle de sucursal para agregar nueva
     */

    private ListadoSucursalViewModel mViewModel;

    private ListView listView;
    private TextView tvSinSucursales;
    private Button bttnAgregarSucursal;
    private ArrayList<SucursalModelo> listaSucursales;
    private AlertDialog alertDialog;

    public static ListadoSucursalFragment newInstance() {
        return new ListadoSucursalFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_listado_sucursal, container, false);
        initComps(root);
        return root;
    }

    private void initComps(View root) {
        listView=root.findViewById(R.id.lv_tiendas);
        tvSinSucursales=root.findViewById(R.id.tv_sin_sucursales_label);
        bttnAgregarSucursal=root.findViewById(R.id.bttn_agrega_sucursal);

        tvSinSucursales.setVisibility(View.GONE);
        bttnAgregarSucursal.setOnClickListener(this);
        AccionesFirebaseRTDataBase.getSucursales(AccionesFirebaseAuth.getUID(),this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ListadoSucursalViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void enInicio() {
        alertDialog = Dialogo.dialogoProceso(getContext(), R.string.msj_cargando_lista_sucursales);
        Dialogo.muestraDialogoProceso(alertDialog);
    }

    @Override
    public void enExito(DataSnapshot respuesta, int accion) {
        listaSucursales=new ArrayList<>();
        for (DataSnapshot dataSnapshot : respuesta.getChildren()) {
                  SucursalModelo sucursalModelo = new SucursalModelo();
                sucursalModelo.setNombre(dataSnapshot.child(Constantes.CONST_SUCURSAL_NOMBRE).getValue().toString());
                sucursalModelo.setDireccion(dataSnapshot.child(Constantes.CONST_SUCURSAL_DIRECCION).getValue().toString());
                sucursalModelo.setHoraAper(dataSnapshot.child(Constantes.CONST_SUCURSAL_HORAAPER).getValue().toString());
                sucursalModelo.setHoraCierre(dataSnapshot.child(Constantes.CONST_SUCURSAL_HORACIERRE).getValue().toString());
                sucursalModelo.setSucursalID(dataSnapshot.child(Constantes.CONST_SUCURSAL_ID).getValue().toString());
                sucursalModelo.setNegocioID(dataSnapshot.child(Constantes.CONST_NEGOCIO_ID).getValue().toString());
                sucursalModelo.setRemoteImg(dataSnapshot.child(Constantes.CONST_BASE_REMOTEIMG).getValue().toString());
                sucursalModelo.setLatitud(Double.parseDouble(dataSnapshot.child(Constantes.CONST_SUCURSAL_LAT).getValue().toString()));
                sucursalModelo.setLongitud(Double.parseDouble(dataSnapshot.child(Constantes.CONST_SUCURSAL_LONG).getValue().toString()));
                listaSucursales.add(sucursalModelo);
        }
        ArrayAdapter<SucursalModelo> adapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1,
                listaSucursales);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        Dialogo.ocultaDialogoProceso(alertDialog);

    }

    @Override
    public void enFallo(Exception excepcion) {
        Dialogo.ocultaDialogoProceso(alertDialog);
        Toast.makeText(this.getContext(), R.string.sin_sucursales_registradas, Toast.LENGTH_LONG).show();
        tvSinSucursales.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle data = new Bundle();
        data.putBoolean(Constantes.CONST_NUEVA_TYPE,false);
        data.putParcelable(Constantes.LLAVE_SUCURSAL, listaSucursales.get(position));

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_sucursales_to_nav_detalle_sucursaln, data);
    }

    @Override
    public void onClick(View v) {
        Bundle data = new Bundle();
        data.putBoolean(Constantes.CONST_NUEVA_TYPE, true);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_sucursales_to_nav_detalle_sucursaln, data);
    }
}