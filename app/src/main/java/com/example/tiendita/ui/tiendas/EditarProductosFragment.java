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

public class EditarProductosFragment extends Fragment {
    /*Permite al negocio agregar, editar o quitar productos de una sucursal */

    private EditarProductosViewModel mViewModel;

    public static EditarProductosFragment newInstance() {
        return new EditarProductosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editar_productos, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditarProductosViewModel.class);
        // TODO: Use the ViewModel
    }

}