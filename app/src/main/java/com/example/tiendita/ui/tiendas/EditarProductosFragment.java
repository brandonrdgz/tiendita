package com.example.tiendita.ui.tiendas;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFireStorage;
import com.example.tiendita.datos.firebase.AccionesFirebaseAuth;
import com.example.tiendita.datos.firebase.AccionesFirebaseRTDataBase;
import com.example.tiendita.datos.firebase.FirebaseCallback;
import com.example.tiendita.datos.firebase.UploadCallback;
import com.example.tiendita.datos.modelos.ProductoModelo;
import com.example.tiendita.datos.modelos.SucursalModelo;
import com.example.tiendita.datos.operaciones.CallbackGeneral;
import com.example.tiendita.utilidades.Constantes;
import com.example.tiendita.utilidades.Dialogo;
import com.example.tiendita.utilidades.ExcepcionUtilidades;
import com.example.tiendita.utilidades.ImageManager;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EditarProductosFragment extends Fragment implements View.OnClickListener {
    /*Permite al negocio agregar, editar o quitar productos de una sucursal */

    private EditarProductosViewModel mViewModel;
    private TextInputEditText nombre, cantidad, precio, presentacion;
    private ImageButton imageButton;
    private Button descartar, guardar;

    public static final int REQUEST_TAKE_PHOTO = 1;
    public static String currentPath;
    private String nombreNegocio, nombreSucursal;
    private String idSucursal, idNegocio;
    public static Uri photoUri;
    private AlertDialog alertDialog;

    public static EditarProductosFragment newInstance() {
        return new EditarProductosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_editar_productos, container, false);
        recuperaDatosSucursal(this.getArguments(), root);
        return root;
    }

    private void recuperaDatosSucursal(Bundle datos, View root) {
        if (datos != null) {
            idSucursal = datos.getString("idSucursal");
            idNegocio = datos.getString("idNegocio");
            nombreNegocio = datos.getString("nombreNegocio");
            nombreSucursal = datos.getString("nombreSucursal");
            Toast.makeText(getContext(), nombreSucursal, Toast.LENGTH_LONG).show();
            initComp(root);
        }
    }

    private void initComp(View root) {
        nombre = root.findViewById(R.id.tf_nombre_producto_editar);
        cantidad = root.findViewById(R.id.tf_cantidad_producto_editar);
        precio = root.findViewById(R.id.tf_precio_producto_editar);
        presentacion = root.findViewById(R.id.tf_presentacion_producto_editar);
        imageButton = root.findViewById(R.id.ib_imagen_producto_editar);
        descartar = root.findViewById(R.id.bttn_descartar_producto_editar);
        guardar = root.findViewById(R.id.bttn_guardar_producto_editar);

        descartar.setOnClickListener(this::onClick);
        guardar.setOnClickListener(this::onClick);
        imageButton.setOnClickListener(this::onClick);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditarProductosViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bttn_descartar_producto_editar:
                limpiarCampos();
                break;
            case R.id.ib_imagen_producto_editar:
                takePhoto();
                break;
            case R.id.bttn_guardar_producto_editar:
                datosDeCampos();
                break;
        }
    }

    private void datosDeCampos() {
        String nombree = nombre.getText().toString();
        int cantidadd = Integer.valueOf(cantidad.getText().toString());
        int precioo = Integer.valueOf(precio.getText().toString());
        String presentacionn = presentacion.getText().toString();

        AccionesFireStorage.loadImage(AccionesFirebaseAuth.getUID(),
                currentPath,
                this.getContext(),
                new UploadCallback<Task<Uri>>() {
                    @Override
                    public void enInicioCar() {
                        alertDialog = Dialogo.dialogoProceso(getContext(), R.string.actualizando_img);
                        Dialogo.muestraDialogoProceso(alertDialog);
                    }

                    @Override
                    public void enExitoCar(Task<Uri> respuesta) {
                        Dialogo.ocultaDialogoProceso(alertDialog);
                        //se actualiza imagen remota y se actualiza el usuario
                        List<String> segments = respuesta.getResult().getPathSegments();
                        ProductoModelo productoNuevo = new ProductoModelo();
                        productoNuevo.setSucursalId(idSucursal);
                        productoNuevo.setNegocioId(idNegocio);
                        productoNuevo.setProductoId(UUID.randomUUID().toString());
                        productoNuevo.setRemoteImg(segments.get(segments.size() - 1));
                        productoNuevo.setNombreProducto(nombree);
                        productoNuevo.setCantidad(cantidadd);
                        productoNuevo.setPrecio(precioo);
                        productoNuevo.setDescripcion(presentacionn);
                        AccionesFirebaseRTDataBase.insertLocalImgRef(productoNuevo.getProductoId(), currentPath, EditarProductosFragment.this.getContext());
                        guardaDatosProducto(productoNuevo,R.string.msj_registro_exitoso);
                    }

                    @Override
                    public void enFalloCar(Exception excepcion) {
                        Dialogo.ocultaDialogoProceso(alertDialog);
                        Toast.makeText(EditarProductosFragment.this.getContext(), R.string.error_actualiza_img, Toast.LENGTH_LONG).show();

                    }
                });
    }


    private void guardaDatosProducto(ProductoModelo productoModelo, int idRecursoMensaje) {
        AccionesFirebaseRTDataBase.guardaProducto(productoModelo, new FirebaseCallback<Void>() {
            @Override
            public void enInicio() {
                alertDialog = Dialogo.dialogoProceso(getContext(), idRecursoMensaje);
                Dialogo.muestraDialogoProceso(alertDialog);
            }

            @Override
            public void enExito(Void respuesta, int accion) {
                Dialogo.ocultaDialogoProceso(alertDialog);
                Snackbar.make(getView(), R.string.msj_guardado_datos_exitoso,
                        Snackbar.LENGTH_LONG).show();

                limpiarCampos();
            }

            @Override
            public void enFallo(Exception excepcion) {
                Dialogo.ocultaDialogoProceso(alertDialog);
                ExcepcionUtilidades.muestraMensajeError(getView(), excepcion, R.string
                        .msj_error_guardar_datos, Constantes.ETIQUETA_DETALLES_SUCURSAL);
            }
        });
    }

    private void limpiarCampos() {
        nombre.setText("");
        cantidad.setText("");
        precio.setText("");
        presentacion.setText("");
        imageButton.setImageResource(R.drawable.img_businness);
    }

    //Para tomar imagen
    public void takePhoto() {
        Intent tomaFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tomaFoto.resolveActivity(this.getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                Toast.makeText(this.getContext(), R.string.no_img, Toast.LENGTH_LONG).show();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this.getContext(), "com.example.tiendita", photoFile);
                tomaFoto.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(tomaFoto, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFile = "JPEG_" + timeStamp + "_";
        File storageDir = this.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File ima = File.createTempFile(imageFile, ".jpg", storageDir);
        currentPath = ima.getAbsolutePath();
        return ima;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            ImageManager.loadImage(currentPath, imageButton, this.getContext());
        }
    }
}