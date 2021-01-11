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
import android.util.Log;
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
import com.example.tiendita.datos.firebase.DownloadCallback;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FileDownloadTask;

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
    private Button descartar, guardar, eliminar, editar;

    public static final int REQUEST_TAKE_PHOTO = 1;
    public static String currentPath;
    private SucursalModelo sucursal;
    public static Uri photoUri;
    private AlertDialog alertDialog;

    ProductoModelo producto;
    private boolean esNuevo, imgHasChange;

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
            sucursal = datos.getParcelable(Constantes.LLAVE_SUCURSAL);
            producto = new ProductoModelo();
            producto.setProductoId(datos.getString("idProducto"));
            producto.setSucursalId(sucursal.getSucursalID());
            producto.setNombreProducto(datos.getString("nombreProducto"));
            producto.setPrecio(datos.getFloat("precioProducto"));
            producto.setCantidad(datos.getInt("cantidadProducto"));
            producto.setDescripcion(datos.getString("descripcionProducto"));
            producto.setRemoteImg(datos.getString("remoteImg"));
            currentPath = producto.getRemoteImg();
            esNuevo = datos.getBoolean("esNuevo");
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
        eliminar = root.findViewById(R.id.bttn_eliminar_producto_editar);
        editar = root.findViewById(R.id.bttn_editar_producto_editar);

        descartar.setOnClickListener(this::onClick);
        guardar.setOnClickListener(this::onClick);
        eliminar.setOnClickListener(this::onClick);
        editar.setOnClickListener(this::onClick);
        imageButton.setOnClickListener(this::onClick);

        if (producto.getNombreProducto() != null) { //si se va a editar un producto
            llenarCampos(producto);
            editarProducto();
            modificarCampos(false);
        } else {
            agregarProducto();
            modificarCampos(true);
        }

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
                llenarCampos(producto);
                modificarCampos(false);
                editarProducto();
                break;
            case R.id.ib_imagen_producto_editar:
                takePhoto();
                break;
            case R.id.bttn_guardar_producto_editar:
                datosDeCampos();
                break;
            case R.id.bttn_editar_producto_editar:
                modificarCampos(true);
                agregarProducto();
                break;
            case R.id.bttn_eliminar_producto_editar:
                eliminarDatosProducto();
                break;
        }
    }

    private void datosDeCampos() {
        if (nombre.getText().toString().trim().isEmpty() ||
                cantidad.getText().toString().trim().isEmpty() ||
                precio.getText().toString().trim().isEmpty() ||
                currentPath.trim().isEmpty() ||
                presentacion.getText().toString().trim().isEmpty()) {
            Snackbar.make(getView(), R.string.msj_datos_vacios,
                    Snackbar.LENGTH_LONG).show();
        } else {
            if (esNuevo == true) {
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
                                productoNuevo.setSucursalId(sucursal.getSucursalID());
                                productoNuevo.setNegocioId(sucursal.getNegocioID());
                                productoNuevo.setProductoId(UUID.randomUUID().toString());
                                productoNuevo.setRemoteImg(segments.get(segments.size() - 1));
                                productoNuevo.setNombreProducto(nombre.getText().toString());
                                productoNuevo.setCantidad(Integer.valueOf(cantidad.getText().toString()));
                                productoNuevo.setPrecio(Float.parseFloat(precio.getText().toString()));
                                productoNuevo.setDescripcion(presentacion.getText().toString());
                                AccionesFirebaseRTDataBase.insertLocalImgRef(productoNuevo.getProductoId(), currentPath, EditarProductosFragment.this.getContext());
                                guardaDatosProducto(productoNuevo, R.string.msj_registro_exitoso);
                                producto = productoNuevo;
                            }

                            @Override
                            public void enFalloCar(Exception excepcion) {
                                Dialogo.ocultaDialogoProceso(alertDialog);
                                Toast.makeText(EditarProductosFragment.this.getContext(), R.string.error_actualiza_img, Toast.LENGTH_LONG).show();

                            }
                        });
            } else {
                if (imgHasChange) {
                    AccionesFireStorage.updateImage(AccionesFirebaseAuth.getUID(), producto.getRemoteImg(),
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
                                    productoNuevo.setSucursalId(sucursal.getSucursalID());
                                    productoNuevo.setNegocioId(sucursal.getNegocioID());
                                    productoNuevo.setProductoId(producto.getProductoId());
                                    productoNuevo.setRemoteImg(segments.get(segments.size() - 1));
                                    productoNuevo.setNombreProducto(nombre.getText().toString());
                                    productoNuevo.setCantidad(Integer.valueOf(cantidad.getText().toString()));
                                    productoNuevo.setPrecio(Float.parseFloat(precio.getText().toString()));
                                    productoNuevo.setDescripcion(presentacion.getText().toString());
                                    AccionesFirebaseRTDataBase.updateLocalImgRef(productoNuevo.getProductoId(), currentPath, EditarProductosFragment.this.getContext());
                                    guardaDatosProducto(productoNuevo, R.string.msj_registro_exitoso);
                                    producto = productoNuevo;
                                }

                                @Override
                                public void enFalloCar(Exception excepcion) {
                                    Dialogo.ocultaDialogoProceso(alertDialog);
                                    Toast.makeText(EditarProductosFragment.this.getContext(), R.string.error_actualiza_img, Toast.LENGTH_LONG).show();

                                }
                            });
                } else {
                    ProductoModelo productoNuevo = new ProductoModelo();
                    productoNuevo.setSucursalId(sucursal.getSucursalID());
                    productoNuevo.setNegocioId(sucursal.getNegocioID());
                    productoNuevo.setProductoId(producto.getProductoId());
                    productoNuevo.setRemoteImg(producto.getRemoteImg());
                    productoNuevo.setNombreProducto(nombre.getText().toString());
                    productoNuevo.setCantidad(Integer.valueOf(cantidad.getText().toString()));
                    productoNuevo.setPrecio(Float.parseFloat(precio.getText().toString()));
                    productoNuevo.setDescripcion(presentacion.getText().toString());
                    guardaDatosProducto(productoNuevo, R.string.msj_registro_exitoso);
                    producto = productoNuevo;
                }
            }
        }
    }

    private void guardaDatosProducto(ProductoModelo productoModelo, int idRecursoMensaje) {
        AccionesFirebaseRTDataBase.guardaProducto(productoModelo, new FirebaseCallback<Void>() {
            @Override
            public void enInicio() {
                alertDialog = Dialogo.dialogoProceso(getContext(), R.string.msj_guardando_datos);
                Dialogo.muestraDialogoProceso(alertDialog);
            }

            @Override
            public void enExito(Void respuesta, int accion) {
                Dialogo.ocultaDialogoProceso(alertDialog);
                Snackbar.make(getView(), R.string.msj_guardado_datos_exitoso,
                        Snackbar.LENGTH_LONG).show();
                modificarCampos(false);
                editarProducto();
            }

            @Override
            public void enFallo(Exception excepcion) {
                Dialogo.ocultaDialogoProceso(alertDialog);
                ExcepcionUtilidades.muestraMensajeError(getView(), excepcion, R.string
                        .msj_error_guardar_datos, Constantes.ETIQUETA_DETALLES_SUCURSAL);
            }
        });
    }


    private void eliminarDatosProducto() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        alertDialogBuilder.setMessage(R.string.msj_eliminar_producto);
        alertDialogBuilder.setPositiveButton(R.string.action_aceptar, (dialog, which) -> {
            //implementar la eliminación de producto
            AccionesFirebaseRTDataBase.deleteProducto(producto.getProductoId(), producto.getSucursalId(),
                    new FirebaseCallback<DataSnapshot>() {
                        @Override
                        public void enInicio() {
                            alertDialog = Dialogo.dialogoProceso(getContext(), R.string.msj_eliminando_producto);
                            Dialogo.muestraDialogoProceso(alertDialog);

                        }

                        @Override
                        public void enExito(DataSnapshot respuesta, int accion) {
                            Dialogo.ocultaDialogoProceso(alertDialog);
                            Toast.makeText(EditarProductosFragment.this.getContext(), R.string.exito_eliminar, Toast.LENGTH_LONG).show();
                            limpiarCampos();
                            editarProducto();
                            modificarCampos(false);
                            modificarBotones(false);
                        }

                        @Override
                        public void enFallo(Exception excepcion) {
                            Dialogo.ocultaDialogoProceso(alertDialog);
                            Toast.makeText(EditarProductosFragment.this.getContext(), R.string.error_eliminar, Toast.LENGTH_LONG).show();

                        }
                    }
            );
        })
                .setNegativeButton(R.string.action_cancelar, null);

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void limpiarCampos() {
        currentPath = "";
        nombre.setText("");
        cantidad.setText("");
        precio.setText("");
        presentacion.setText("");
        imageButton.setImageResource(R.drawable.img_businness);
    }

    private void llenarCampos(ProductoModelo producto) {
        nombre.setText(producto.getNombreProducto() + "");
        cantidad.setText(producto.getCantidad() + "");
        precio.setText(producto.getCantidad() + "");
        presentacion.setText(producto.getDescripcion() + "");
        imgHasChange = false;

        String localRef = AccionesFirebaseRTDataBase.getLocalImgRef(this.producto.getProductoId(), getContext());
        if (localRef != null) {
            File filePhoto = new File(localRef);
            if (filePhoto.exists()) {
                ImageManager.loadImage(localRef, imageButton, this.getContext());
                nombre.setText(this.producto.getNombreProducto() + "");
                precio.setText(this.producto.getPrecio() + "");
                cantidad.setText(this.producto.getCantidad() + "");
                presentacion.setText(this.producto.getDescripcion() + "");
                descartar.setVisibility(View.GONE);
            } else {
                AccionesFireStorage.downloadImg(this.producto.getRemoteImg(),
                        getActivity(),
                        getContext(),
                        new DownloadCallback<Task<FileDownloadTask.TaskSnapshot>>() {
                            @Override
                            public void enInicioDesc() {
                                alertDialog = Dialogo.dialogoProceso(EditarProductosFragment.this.getContext(), R.string.msj_operacion_datos);
                                Dialogo.muestraDialogoProceso(alertDialog);
                            }

                            @Override
                            public void enExitoDesc(Task<FileDownloadTask.TaskSnapshot> respuesta, File localFile) {
                                Dialogo.ocultaDialogoProceso(alertDialog);
                                String localRef = AccionesFirebaseRTDataBase.getLocalImgRef(producto.getProductoId(), getContext());
                                ImageManager.loadImage(localRef, imageButton, EditarProductosFragment.this.getContext());
                                nombre.setText(producto.getNombreProducto() + "");
                                precio.setText(producto.getPrecio() + "");
                                cantidad.setText(producto.getCantidad() + "");
                                presentacion.setText(producto.getDescripcion() + "");
                                descartar.setVisibility(View.GONE);

                            }

                            @Override
                            public void enFalloDesc(Exception excepcion) {
                                Dialogo.ocultaDialogoProceso(alertDialog);
                                Toast.makeText(EditarProductosFragment.this.getContext(), R.string.error_cargar_img, Toast.LENGTH_LONG).show();
                                Log.d("Descargar imagen", "Error al descargar imagen\n Causa: " + excepcion.getCause());

                            }
                        },
                        this.producto.getProductoId());
            }
        } else {
            AccionesFireStorage.downloadImg(this.producto.getRemoteImg(),
                    getActivity(),
                    getContext(),
                    new DownloadCallback<Task<FileDownloadTask.TaskSnapshot>>() {
                        @Override
                        public void enInicioDesc() {
                            alertDialog = Dialogo.dialogoProceso(EditarProductosFragment.this.getContext(), R.string.msj_operacion_datos);
                            Dialogo.muestraDialogoProceso(alertDialog);
                        }

                        @Override
                        public void enExitoDesc(Task<FileDownloadTask.TaskSnapshot> respuesta, File localFile) {
                            Dialogo.ocultaDialogoProceso(alertDialog);
                            String localRef = AccionesFirebaseRTDataBase.getLocalImgRef(producto.getProductoId(), getContext());
                            ImageManager.loadImage(localRef, imageButton, EditarProductosFragment.this.getContext());
                            nombre.setText(producto.getNombreProducto() + "");
                            precio.setText(producto.getPrecio() + "");
                            cantidad.setText(producto.getCantidad() + "");
                            presentacion.setText(producto.getDescripcion() + "");
                            descartar.setVisibility(View.GONE);
                        }

                        @Override
                        public void enFalloDesc(Exception excepcion) {
                            Dialogo.ocultaDialogoProceso(alertDialog);
                            Toast.makeText(EditarProductosFragment.this.getContext(), R.string.error_cargar_img, Toast.LENGTH_LONG).show();
                            Log.d("Descargar imagen", "Error al descargar imagen\n Causa: " + excepcion.getCause());
                        }
                    },
                    this.producto.getProductoId());
        }
    }

    private void modificarCampos(boolean activoOinactivo) {
        nombre.setEnabled(activoOinactivo);
        cantidad.setEnabled(activoOinactivo);
        precio.setEnabled(activoOinactivo);
        presentacion.setEnabled(activoOinactivo);
        imageButton.setEnabled(activoOinactivo);
    }

    //Para cambios de botones
    private void agregarProducto() {
        eliminar.setVisibility(View.GONE);
        editar.setVisibility(View.GONE);
        descartar.setVisibility(View.VISIBLE);
        guardar.setVisibility(View.VISIBLE);
    }

    private void editarProducto() {
        eliminar.setVisibility(View.VISIBLE);
        editar.setVisibility(View.VISIBLE);
        descartar.setVisibility(View.GONE);
        guardar.setVisibility(View.GONE);
    }

    private void modificarBotones(boolean trueORfalse){
        eliminar.setEnabled(trueORfalse);
        descartar.setEnabled(trueORfalse);
        editar.setEnabled(trueORfalse);
        guardar.setEnabled(trueORfalse);
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
            imgHasChange = true;
        }
    }


}