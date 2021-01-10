package com.example.tiendita.ui.tiendas;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.tiendita.R;
import com.google.android.material.textfield.TextInputEditText;

public class EditarProductosFragment extends Fragment implements View.OnClickListener {
    /*Permite al negocio agregar, editar o quitar productos de una sucursal */

    private EditarProductosViewModel mViewModel;
    private TextInputEditText nombre, cantidad, precio, presentacion;
    private ImageButton imageButton;
    private Button descartar, guardar;

    public static EditarProductosFragment newInstance() {
        return new EditarProductosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_editar_productos, container, false);
        initComp(root);
        return root;
    }
    private void initComp(View root){
        nombre = root.findViewById(R.id.tf_nombre_producto_editar);
        cantidad = root.findViewById(R.id.tf_cantidad_producto_editar);
        precio = root.findViewById(R.id.tf_cantidad_producto_editar);
        presentacion = root.findViewById(R.id.tf_presentacion_producto_editar);
        imageButton = root.findViewById(R.id.ib_imagen_producto_editar);
        descartar = root.findViewById(R.id.bttn_descartar_producto_editar);
        guardar = root.findViewById(R.id.bttn_guardar_producto_editar);


    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditarProductosViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bttn_descartar_producto_editar:
limpiarCampos();
                break;
        }
    }

    private void limpiarCampos(){
        nombre.setText("");
        cantidad.setText("");
        precio.setText("");
        presentacion.setText("");
        imageButton.setImageResource(0);
    }
}