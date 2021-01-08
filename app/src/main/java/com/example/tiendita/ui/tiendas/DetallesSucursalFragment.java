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

public class DetallesSucursalFragment extends Fragment {
    /*El gramento se utiliza en 3 formas:
    Muestra el detalle de una sucursal al usuario, desde el fragmento mapa de usuario:
        Permite al usuairo hacer un pedido: redirige al usuairo al frgamento editar pedido
    Muestra el detalle de una sucursal al negocio, desde lista de suscursales
        Permite al negocio editar los datos de su sucursal en el mismo fragmento
    Guarda una nueva sucursal, desde la lista de sucursales-accion agregar nueva
    **Solo cuando el usuairo es negocio redirecciona al frgamento editar productos
     */
    private DetallesSucursalViewModel mViewModel;

    public static DetallesSucursalFragment newInstance() {
        return new DetallesSucursalFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detalles_sucursal, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DetallesSucursalViewModel.class);
        // TODO: Use the ViewModel
    }

}