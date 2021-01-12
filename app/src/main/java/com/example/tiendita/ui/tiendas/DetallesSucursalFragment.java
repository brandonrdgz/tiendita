package com.example.tiendita.ui.tiendas;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFireStorage;
import com.example.tiendita.datos.firebase.AccionesFirebaseAuth;
import com.example.tiendita.datos.firebase.AccionesFirebaseRTDataBase;
import com.example.tiendita.datos.firebase.DownloadCallback;
import com.example.tiendita.datos.firebase.FirebaseCallback;
import com.example.tiendita.datos.firebase.UploadCallback;
import com.example.tiendita.datos.modelos.SucursalModelo;
import com.example.tiendita.datos.operaciones.CallbackGeneral;
import com.example.tiendita.text_watcher.CampoTextWatcher;
import com.example.tiendita.utilidades.Constantes;
import com.example.tiendita.utilidades.Dialogo;
import com.example.tiendita.utilidades.ExcepcionUtilidades;
import com.example.tiendita.utilidades.ImageManager;
import com.example.tiendita.utilidades.Validaciones;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class DetallesSucursalFragment extends Fragment implements View.OnClickListener {
    /*El gramento se utiliza en 3 formas:
    Muestra el detalle de una sucursal al usuario, desde el fragmento mapa de usuario:
        Permite al usuairo hacer un pedido: redirige al usuairo al frgamento editar pedido
    Muestra el detalle de una sucursal al negocio, desde lista de suscursales
        Permite al negocio editar los datos de su sucursal en el mismo fragmento
    Guarda una nueva sucursal, desde la lista de sucursales-accion agregar nueva
    **Solo cuando el usuairo es negocio redirecciona al frgamento editar productos
     */
    private ImageView ibImgSucursal;
    private TextInputLayout tilNombre;
    private TextInputLayout tilDireccion;
    private TextInputLayout tilHoraApertura;
    private TextInputLayout tilHoraCierre;
    private MaterialButton mbRealizarPedido;
    private MaterialButton mbAdminProductos;
    private LinearLayout llEliminarEditar;
    private MaterialButton mbEliminarSucursal;
    private MaterialButton mbEditarSucursal;
    private LinearLayout llDescartarGuardar;
    private MaterialButton mbDescartar;
    private MaterialButton mbGuardar;
    private DetallesSucursalViewModel mViewModel;
    public static final int REQUEST_TAKE_PHOTO=1;
    public static  String currentPath;
    public static Uri photoUri;


    private boolean esUsuario;
    private boolean esNegocio;
    private boolean esSucursalNueva;
    private boolean imgHasChange;
    private AlertDialog alertDialog;

    private SucursalModelo sucursal;
    private TextInputLayout[] textInputLayouts;

    public static DetallesSucursalFragment newInstance() {
        return new DetallesSucursalFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detalles_sucursal, container, false);

        recuperaDatosSucursal(this.getArguments(),root);


        return root;
    }

    private void recuperaDatosSucursal(Bundle datos,View root) {
        if (datos != null) {
            esNegocio = datos.getBoolean(Constantes.CONST_NEGOCIO_TYPE);
            esSucursalNueva = datos.getBoolean(Constantes.CONST_NUEVA_TYPE);
            sucursal = datos.getParcelable(Constantes.LLAVE_SUCURSAL);
            esUsuario = !esNegocio;
            iniComponentes(root);
        }
    }

    private void iniComponentes(View root) {
        ibImgSucursal = root.findViewById(R.id.ib_img_sucursal_det_sucursal);
        tilNombre = root.findViewById(R.id.til_nombre_sucursal_det_sucursal);
        tilDireccion = root.findViewById(R.id.til_direccion_det_sucursal);
        tilHoraApertura = root.findViewById(R.id.til_hora_apertura_det_sucursal);
        tilHoraCierre = root.findViewById(R.id.til_hora_cierre_det_sucursal);
        mbRealizarPedido = root.findViewById(R.id.mb_realizar_pedido_det_sucursal);
        mbAdminProductos = root.findViewById(R.id.mb_admin_productos_det_sucursal);
        llEliminarEditar = root.findViewById(R.id.ll_elim_edit_sucursal_det_sucursal);
        mbEliminarSucursal = root.findViewById(R.id.mb_eliminar_det_sucursal);
        mbEditarSucursal = root.findViewById(R.id.mb_editar_det_sucursal);
        llDescartarGuardar = root.findViewById(R.id.ll_desc_guard_datos_sucursal_det_sucursal);
        mbDescartar = root.findViewById(R.id.mb_descartar_det_sucursal);
        mbGuardar = root.findViewById(R.id.mb_guardar_det_sucursal);

        textInputLayouts = new TextInputLayout[] {
          tilNombre,
          tilDireccion,
          tilHoraApertura,
          tilHoraCierre
        };

        for (TextInputLayout textInputLayout : textInputLayouts) {
            textInputLayout.getEditText().addTextChangedListener(new CampoTextWatcher(this.getActivity(),
               textInputLayout));
        }

        if (esUsuario) {
            ibImgSucursal.setClickable(false);
            llenaCamposDatosSucursal();
            deshabilitaCamposDatosSucursal();
            mbAdminProductos.setVisibility(View.GONE);
            llEliminarEditar.setVisibility(View.GONE);
            llDescartarGuardar.setVisibility(View.GONE);
        }
        else if (esSucursalNueva) {
            mbRealizarPedido.setVisibility(View.GONE);
            mbAdminProductos.setVisibility(View.GONE);
            llEliminarEditar.setVisibility(View.GONE);
        }
        else {
            imgHasChange=false;
            llenaCamposDatosSucursal();
            deshabilitaCamposDatosSucursal();
            mbRealizarPedido.setVisibility(View.GONE);
            llDescartarGuardar.setVisibility(View.GONE);
        }

        ibImgSucursal.setOnClickListener(this);
        mbRealizarPedido.setOnClickListener(this);
        mbAdminProductos.setOnClickListener(this);
        mbEliminarSucursal.setOnClickListener(this);
        mbEditarSucursal.setOnClickListener(this);
        mbDescartar.setOnClickListener(this);
        mbGuardar.setOnClickListener(this);
    }

    private void llenaCamposDatosSucursal() {
        String localRef = AccionesFirebaseRTDataBase.getLocalImgRef(this.sucursal.getSucursalID(), getContext());
        if(localRef!=null) {
            File filePhoto = new File(localRef);
            if (filePhoto.exists()) {
                ImageManager.loadImage(localRef, ibImgSucursal, this.getContext());
                tilNombre.getEditText().setText(this.sucursal.getNombre());
                tilDireccion.getEditText().setText(this.sucursal.getDireccion());
                tilHoraApertura.getEditText().setText(this.sucursal.getHoraAper());
                tilHoraCierre.getEditText().setText(this.sucursal.getHoraCierre());
                llDescartarGuardar.setVisibility(View.GONE);
            } else {
                AccionesFireStorage.downloadImg(this.sucursal.getRemoteImg(),
                        getActivity(),
                        getContext(),
                        new DownloadCallback<Task<FileDownloadTask.TaskSnapshot>>() {
                            @Override
                            public void enInicioDesc() {
                                alertDialog = Dialogo.dialogoProceso(DetallesSucursalFragment.this.getContext(), R.string.msj_operacion_datos);
                                Dialogo.muestraDialogoProceso(alertDialog);


                            }

                            @Override
                            public void enExitoDesc(Task<FileDownloadTask.TaskSnapshot> respuesta, File localFile) {
                                Dialogo.ocultaDialogoProceso(alertDialog);
                                String localRef = AccionesFirebaseRTDataBase.getLocalImgRef(sucursal.getSucursalID(), getContext());
                                ImageManager.loadImage(localRef, ibImgSucursal, DetallesSucursalFragment.this.getContext());
                                tilNombre.getEditText().setText(sucursal.getNombre());
                                tilDireccion.getEditText().setText(sucursal.getDireccion());
                                tilHoraApertura.getEditText().setText(sucursal.getHoraAper());
                                tilHoraCierre.getEditText().setText(sucursal.getHoraCierre());
                                llDescartarGuardar.setVisibility(View.GONE);

                            }

                            @Override
                            public void enFalloDesc(Exception excepcion) {
                                Dialogo.ocultaDialogoProceso(alertDialog);
                                Toast.makeText(DetallesSucursalFragment.this.getContext(), R.string.error_cargar_img, Toast.LENGTH_LONG).show();
                                Log.d("Descargar imagen", "Error al descargar imagen\n Causa: " + excepcion.getCause());

                            }
                        },
                        this.sucursal.getSucursalID());
            }
        }
        else{
            AccionesFireStorage.downloadImg(this.sucursal.getRemoteImg(),
                    getActivity(),
                    getContext(),
                    new DownloadCallback<Task<FileDownloadTask.TaskSnapshot>>() {
                        @Override
                        public void enInicioDesc() {
                            alertDialog = Dialogo.dialogoProceso(DetallesSucursalFragment.this.getContext(), R.string.msj_operacion_datos);
                            Dialogo.muestraDialogoProceso(alertDialog);


                        }

                        @Override
                        public void enExitoDesc(Task<FileDownloadTask.TaskSnapshot> respuesta, File localFile) {
                            Dialogo.ocultaDialogoProceso(alertDialog);
                            String localRef = AccionesFirebaseRTDataBase.getLocalImgRef(sucursal.getSucursalID(), getContext());
                            ImageManager.loadImage(localRef, ibImgSucursal, DetallesSucursalFragment.this.getContext());
                            tilNombre.getEditText().setText(sucursal.getNombre());
                            tilDireccion.getEditText().setText(sucursal.getDireccion());
                            tilHoraApertura.getEditText().setText(sucursal.getHoraAper());
                            tilHoraCierre.getEditText().setText(sucursal.getHoraCierre());
                            llDescartarGuardar.setVisibility(View.GONE);

                        }

                        @Override
                        public void enFalloDesc(Exception excepcion) {
                            Dialogo.ocultaDialogoProceso(alertDialog);
                            Toast.makeText(DetallesSucursalFragment.this.getContext(), R.string.error_cargar_img, Toast.LENGTH_LONG).show();
                            Log.d("Descargar imagen", "Error al descargar imagen\n Causa: " + excepcion.getCause());

                        }
                    },
                    this.sucursal.getSucursalID());

        }
    }

    private void deshabilitaCamposDatosSucursal() {
        tilNombre.setEnabled(false);
        tilDireccion.setEnabled(false);
        tilHoraApertura.setEnabled(false);
        tilHoraCierre.setEnabled(false);
        imgHasChange=false;
    }

    private void habilitaCamposDatosSucursal() {
        tilNombre.setEnabled(true);
        tilDireccion.setEnabled(true);
        tilHoraApertura.setEnabled(true);
        tilHoraCierre.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_img_sucursal_det_sucursal:
                ibImgSucursalClic();
                break;

            case R.id.mb_realizar_pedido_det_sucursal:
                mbRealizarPedidoClic();
                break;

            case R.id.mb_admin_productos_det_sucursal:
                mbAdminProductosClic();
                break;

            case R.id.mb_eliminar_det_sucursal:
                mbEliminarSucursalClic();
                break;

            case R.id.mb_editar_det_sucursal:
                mbEditarSucursalClic();
                break;

            case R.id.mb_descartar_det_sucursal:
                mbDescartarClic();
                break;

            case R.id.mb_guardar_det_sucursal:
                mbGuardarClic();
                break;
        }
    }

    private void ibImgSucursalClic() {
        //Implementar logica de clic en la imagen de la sucursal
        takePhoto();
    }

    private void mbRealizarPedidoClic() {
        Bundle datos = new Bundle();
        datos.putBoolean(Constantes.CONST_EDICION_TYPE, false);
        datos.putString(Constantes.CONST_SUCURSAL_ID, sucursal.getSucursalID());
        datos.putParcelable(Constantes.LLAVE_SUCURSAL, sucursal);
        NavHostFragment.findNavController(this)
           .navigate(R.id.action_nav_detalle_sucursalu_to_nav_editpedido, datos);
    }

    private void mbAdminProductosClic() {
        //Implementar la acción para administrar los productos de la sucursal
        Bundle data = new Bundle();
        data.putParcelable(Constantes.LLAVE_SUCURSAL, sucursal);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_detalle_sucursaln_to_nav_listado_productos, data);
    }

    private void mbEliminarSucursalClic() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        alertDialogBuilder.setMessage(R.string.msj_eliminar_sucursal);
        alertDialogBuilder.setPositiveButton(R.string.action_aceptar, (dialog, which) -> {
            //implementar la eliminación tanto de la sucursal como de sus productos
            AccionesFirebaseRTDataBase.deleteSucursal(AccionesFirebaseAuth.getUID(),
                    sucursal.getSucursalID(),
                    new FirebaseCallback<DataSnapshot>() {
                        @Override
                        public void enInicio() {
                            alertDialog = Dialogo.dialogoProceso(getContext(), R.string.msj_elimindo_sucursal);
                            Dialogo.muestraDialogoProceso(alertDialog);

                        }

                        @Override
                        public void enExito(DataSnapshot respuesta, int accion) {
                            Dialogo.ocultaDialogoProceso(alertDialog);
                            Toast.makeText(DetallesSucursalFragment.this.getContext(), R.string.exito_eliminar, Toast.LENGTH_LONG).show();
                            AccionesFirebaseRTDataBase.deleteProductos(sucursal.getSucursalID(), new FirebaseCallback<DataSnapshot>() {
                                @Override
                                public void enInicio() {

                                }

                                @Override
                                public void enExito(DataSnapshot respuesta, int accion) {

                                }

                                @Override
                                public void enFallo(Exception excepcion) {
                                    Log.d("Error Eliminar","Error al elimnar productos\n Causa: "+excepcion.getLocalizedMessage());

                                }
                            });
                            AccionesFirebaseRTDataBase.deletePedido(sucursal.getSucursalID(), new FirebaseCallback<DataSnapshot>() {
                                @Override
                                public void enInicio() {

                                }

                                @Override
                                public void enExito(DataSnapshot respuesta, int accion) {

                                }

                                @Override
                                public void enFallo(Exception excepcion) {
                                    Log.d("Error Eliminar","Error al elimnar productos\n Causa: "+excepcion.getLocalizedMessage());

                                }
                            });
                            ibImgSucursal.setImageResource(R.drawable.img_businness);
                            tilNombre.getEditText().setText("");
                            tilNombre.setError("");
                            tilDireccion.getEditText().setText("");
                            tilDireccion.setError("");
                            tilHoraApertura.getEditText().setText("");
                            tilHoraApertura.setError("");
                            tilHoraCierre.getEditText().setText("");
                            tilHoraCierre.setError("");
                        }

                        @Override
                        public void enFallo(Exception excepcion) {
                            Dialogo.ocultaDialogoProceso(alertDialog);
                            Toast.makeText(DetallesSucursalFragment.this.getContext(), R.string.error_eliminar, Toast.LENGTH_LONG).show();

                        }
                    }
            );
        })
        .setNegativeButton(R.string.action_cancelar, null);

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void mbEditarSucursalClic() {
        habilitaCamposDatosSucursal();
        mbAdminProductos.setVisibility(View.GONE);
        llEliminarEditar.setVisibility(View.GONE);
        llDescartarGuardar.setVisibility(View.VISIBLE);
    }

    private void mbDescartarClic() {
        if (esSucursalNueva) {
            this.getActivity().onBackPressed();
        }
        else {
            llenaCamposDatosSucursal();
            deshabilitaCamposDatosSucursal();
            llDescartarGuardar.setVisibility(View.GONE);
            mbAdminProductos.setVisibility(View.VISIBLE);
            llEliminarEditar.setVisibility(View.VISIBLE);
        }
    }

    private void mbGuardarClic() {
        if (haTomadoFoto() && camposValidos()) {
            datosDeCampos();
        }
    }

    private boolean haTomadoFoto() {
        boolean fotoTomada = currentPath != null;
        boolean fotoNoTomada = ! fotoTomada;

        if (fotoNoTomada) {
            Snackbar.make(this.getView(), R.string.msj_error_foto_no_tomada, Snackbar.LENGTH_LONG)
               .show();
        }

        return fotoTomada;
    }

    private boolean camposValidos() {
        boolean camposValidos = true;
        Validaciones.validaCampos(this.getActivity(), textInputLayouts);

        for (TextInputLayout textInputLayout : textInputLayouts) {
            if (textInputLayout.getError() != null) {
                camposValidos = false;
                break;
            }
        }

        return camposValidos;
    }

    private void datosDeCampos() {
        String nombre = tilNombre.getEditText().getText().toString();
        String direccion = tilDireccion.getEditText().getText().toString();
        String horaApertura = tilHoraApertura.getEditText().getText().toString();
        String horaCierre = tilHoraCierre.getEditText().getText().toString();

        if (esSucursalNueva) {
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
                            SucursalModelo sucursalNueva = new SucursalModelo();
                            sucursalNueva.setNegocioID(AccionesFirebaseAuth.getUID());
                            sucursalNueva.setSucursalID(UUID.randomUUID().toString());
                            sucursalNueva.setRemoteImg(segments.get(segments.size() - 1));
                            sucursalNueva.setNombre(nombre);
                            sucursalNueva.setDireccion(direccion);
                            sucursalNueva.setHoraAper(horaApertura);
                            sucursalNueva.setHoraCierre(horaCierre);
                            AccionesFirebaseRTDataBase.insertLocalImgRef(sucursalNueva.getSucursalID(), currentPath, DetallesSucursalFragment.this.getContext());
                            latitudLongitudDeDireccion(sucursalNueva.getDireccion(), new CallbackGeneral<Address>() {
                                @Override
                                public void enInicio() {
                                    alertDialog = Dialogo.dialogoProceso(getContext(), R.string.msj_generando_coordenadas);
                                    Dialogo.muestraDialogoProceso(alertDialog);
                                }

                                @Override
                                public void enExito(Address respuesta) {
                                    Dialogo.ocultaDialogoProceso(alertDialog);

                                    sucursalNueva.setLatitud(respuesta.getLatitude());
                                    sucursalNueva.setLongitud(respuesta.getLongitude());

                                    guardaDatosSucursal(sucursalNueva, R.string.msj_guardando_datos);
                                }

                                @Override
                                public void enFallo(Exception excepcion) {
                                    Dialogo.ocultaDialogoProceso(alertDialog);
                                    ExcepcionUtilidades.muestraMensajeError(getView(), excepcion,
                                            R.string.msj_error_generacion_coordenadas, Constantes.ETIQUETA_DETALLES_SUCURSAL);
                                }
                            });


                        }

                        @Override
                        public void enFalloCar(Exception excepcion) {
                            Dialogo.ocultaDialogoProceso(alertDialog);
                            Toast.makeText(DetallesSucursalFragment.this.getContext(), R.string.error_actualiza_img,Toast.LENGTH_LONG).show();

                        }
                    });

        }
        else {
            //si se cambio la imagen de usuario se actualiza la referencia remota
            if (imgHasChange) {

                AccionesFireStorage.updateImage(AccionesFirebaseAuth.getUID(),
                        sucursal.getRemoteImg(),
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
                                List<String> segments = respuesta.getResult().getPathSegments();
                                SucursalModelo sucursalEditada = new SucursalModelo();
                                sucursalEditada.setNegocioID(sucursal.getNegocioID());
                                sucursalEditada.setSucursalID(sucursal.getSucursalID());
                                sucursalEditada.setRemoteImg(segments.get(segments.size() - 1));
                                sucursalEditada.setNombre(nombre);
                                sucursalEditada.setHoraAper(horaApertura);
                                sucursalEditada.setHoraCierre(horaCierre);
                                sucursalEditada.setDireccion(sucursal.getDireccion());
                                AccionesFirebaseRTDataBase.updateLocalImgRef(sucursal.getSucursalID(), currentPath, DetallesSucursalFragment.this.getContext());
                                boolean direccionHaCambiado = ! sucursal.getDireccion().equals(direccion);

                                if (direccionHaCambiado) {
                                    sucursalEditada.setDireccion(direccion);

                                    latitudLongitudDeDireccion(sucursalEditada.getDireccion(), new CallbackGeneral<Address>() {
                                        @Override
                                        public void enInicio() {
                                            alertDialog = Dialogo.dialogoProceso(getContext(), R.string.msj_generando_coordenadas);
                                            Dialogo.muestraDialogoProceso(alertDialog);
                                        }

                                        @Override
                                        public void enExito(Address respuesta) {
                                            Dialogo.ocultaDialogoProceso(alertDialog);

                                            sucursalEditada.setLatitud(respuesta.getLatitude());
                                            sucursalEditada.setLongitud(respuesta.getLongitude());

                                            guardaDatosSucursal(sucursalEditada, R.string.msj_guardando_datos);
                                        }

                                        @Override
                                        public void enFallo(Exception excepcion) {
                                            Dialogo.ocultaDialogoProceso(alertDialog);
                                            ExcepcionUtilidades.muestraMensajeError(getView(), excepcion,
                                                    R.string.msj_error_generacion_coordenadas, Constantes.ETIQUETA_DETALLES_SUCURSAL);
                                        }
                                    });
                                }
                                else {
                                    sucursalEditada.setLatitud(sucursal.getLatitud());
                                    sucursalEditada.setLongitud(sucursal.getLongitud());
                                    guardaDatosSucursal(sucursalEditada, R.string.msj_guardando_datos);
                                }

                            }

                            @Override
                            public void enFalloCar(Exception excepcion) {
                                Dialogo.ocultaDialogoProceso(alertDialog);
                                Toast.makeText(DetallesSucursalFragment.this.getContext(), R.string.error_actualiza_img,Toast.LENGTH_LONG).show();


                            }
                        });
            }else {
                SucursalModelo sucursalEditada = new SucursalModelo();
                sucursalEditada.setNegocioID(this.sucursal.getNegocioID());
                sucursalEditada.setSucursalID(this.sucursal.getSucursalID());
                sucursalEditada.setRemoteImg(this.sucursal.getRemoteImg());
                sucursalEditada.setNombre(nombre);
                sucursalEditada.setHoraAper(horaApertura);
                sucursalEditada.setHoraCierre(horaCierre);
                sucursalEditada.setDireccion(this.sucursal.getDireccion());
                boolean direccionHaCambiado = !this.sucursal.getDireccion().equals(direccion);

                if (direccionHaCambiado) {
                    sucursalEditada.setDireccion(direccion);

                    latitudLongitudDeDireccion(sucursalEditada.getDireccion(), new CallbackGeneral<Address>() {
                        @Override
                        public void enInicio() {
                            alertDialog = Dialogo.dialogoProceso(getContext(), R.string.msj_generando_coordenadas);
                            Dialogo.muestraDialogoProceso(alertDialog);
                        }

                        @Override
                        public void enExito(Address respuesta) {
                            Dialogo.ocultaDialogoProceso(alertDialog);

                            sucursalEditada.setLatitud(respuesta.getLatitude());
                            sucursalEditada.setLongitud(respuesta.getLongitude());

                            guardaDatosSucursal(sucursalEditada, R.string.msj_guardando_datos);
                        }

                        @Override
                        public void enFallo(Exception excepcion) {
                            Dialogo.ocultaDialogoProceso(alertDialog);
                            ExcepcionUtilidades.muestraMensajeError(getView(), excepcion,
                                    R.string.msj_error_generacion_coordenadas, Constantes.ETIQUETA_DETALLES_SUCURSAL);
                        }
                    });
                } else {
                    sucursalEditada.setLatitud(sucursal.getLatitud());
                    sucursalEditada.setLongitud(sucursal.getLongitud());
                    guardaDatosSucursal(sucursalEditada, R.string.msj_guardando_datos);
                }
            }
        }
    }

    private void latitudLongitudDeDireccion(String direccion, CallbackGeneral<Address> callbackGeneral) {
        callbackGeneral.enInicio();

        getActivity().runOnUiThread(() -> {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addressList;

            try {
                addressList = geocoder.getFromLocationName(direccion, 1);
                callbackGeneral.enExito(addressList.get(0));
            }
            catch (IOException | IndexOutOfBoundsException ex) {
                callbackGeneral.enFallo(ex);
            }
        });
    }

    private void guardaDatosSucursal(SucursalModelo sucursalModelo, int idRecursoMensaje) {
        AccionesFirebaseRTDataBase.guardaSucursal(sucursalModelo, new FirebaseCallback<Void>() {
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

                if (esSucursalNueva) {
                    esSucursalNueva = false;
                }

                sucursal = sucursalModelo;
                llDescartarGuardar.setVisibility(View.GONE);
                mbAdminProductos.setVisibility(View.VISIBLE);
                llEliminarEditar.setVisibility(View.VISIBLE);
                deshabilitaCamposDatosSucursal();
            }

            @Override
            public void enFallo(Exception excepcion) {
                Dialogo.ocultaDialogoProceso(alertDialog);
                ExcepcionUtilidades.muestraMensajeError(getView(), excepcion, R.string
                .msj_error_guardar_datos, Constantes.ETIQUETA_DETALLES_SUCURSAL);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DetallesSucursalViewModel.class);
        // TODO: Use the ViewModel
    }

    public  void takePhoto() {
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

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==REQUEST_TAKE_PHOTO && resultCode== Activity.RESULT_OK){
            ImageManager.loadImage(currentPath, ibImgSucursal,this.getContext());
            imgHasChange=true;
        }
    }

    public File createImageFile()throws IOException {
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFile="JPEG_"+timeStamp+"_";
        File storageDir=this.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File ima=File.createTempFile(imageFile,".jpg",storageDir);
        currentPath=ima.getAbsolutePath();
        return ima;
    }

}