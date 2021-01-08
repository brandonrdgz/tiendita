package com.example.tiendita.ui.tiendas;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tiendita.R;

public class ListadoSucursalFragment extends Fragment {
    /*Muestra el listado de sucursales de un negocio
      Al elegir un item de la lista redirecciona a la vista detalles sucursal para visualizar o editar datos
      Al seleccionar la opcion nueva redirije a la vista detalle de sucursal para agregar nueva
     */

    private ListadoSucursalViewModel mViewModel;

    public static ListadoSucursalFragment newInstance() {
        return new ListadoSucursalFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listado_sucursal, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ListadoSucursalViewModel.class);
        // TODO: Use the ViewModel
    }

}