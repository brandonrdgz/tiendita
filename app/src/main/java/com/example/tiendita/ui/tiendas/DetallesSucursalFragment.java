package com.example.tiendita.ui.tiendas;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFirebaseAuth;
import com.example.tiendita.datos.firebase.AccionesFirebaseRTDataBase;
import com.example.tiendita.datos.firebase.FirebaseCallback;
import com.example.tiendita.datos.modelos.SucursalModelo;
import com.example.tiendita.datos.operaciones.CallbackGeneral;
import com.example.tiendita.text_watcher.CampoTextWatcher;
import com.example.tiendita.utilidades.Constantes;
import com.example.tiendita.utilidades.Dialogo;
import com.example.tiendita.utilidades.ExcepcionUtilidades;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetallesSucursalFragment extends Fragment implements View.OnClickListener {
    /*El gramento se utiliza en 3 formas:
    Muestra el detalle de una sucursal al usuario, desde el fragmento mapa de usuario:
        Permite al usuairo hacer un pedido: redirige al usuairo al frgamento editar pedido
    Muestra el detalle de una sucursal al negocio, desde lista de suscursales
        Permite al negocio editar los datos de su sucursal en el mismo fragmento
    Guarda una nueva sucursal, desde la lista de sucursales-accion agregar nueva
    **Solo cuando el usuairo es negocio redirecciona al frgamento editar productos
     */
    private ImageButton ibImgSucursal;
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

    private boolean esUsuario;
    private boolean esNegocio;
    private boolean esSucursalNueva;
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

        recuperaDatosSucursal(this.getArguments());
        iniComponentes(root);

        return root;
    }

    private void recuperaDatosSucursal(Bundle datos) {
        if (datos != null) {
            esNegocio = datos.getBoolean(Constantes.CONST_NEGOCIO_TYPE);
            esSucursalNueva = datos.getBoolean(Constantes.CONST_NUEVA_TYPE);
        }

        sucursal = datos.getParcelable(Constantes.LLAVE_SUCURSAL);
        esUsuario = ! esNegocio;
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
        tilNombre.getEditText().setText(this.sucursal.getNombre());
        tilDireccion.getEditText().setText(this.sucursal.getDireccion());
        tilHoraApertura.getEditText().setText(this.sucursal.getHoraAper());
        tilHoraCierre.getEditText().setText(this.sucursal.getHoraCierre());
        llDescartarGuardar.setVisibility(View.GONE);
    }

    private void deshabilitaCamposDatosSucursal() {
        tilNombre.setEnabled(false);
        tilDireccion.setEnabled(false);
        tilHoraApertura.setEnabled(false);
        tilHoraCierre.setEnabled(false);
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
    }

    private void mbRealizarPedidoClic() {
        Bundle datos = new Bundle();
        datos.putBoolean(Constantes.CONST_EDICION_TYPE, false);
        datos.putString(Constantes.CONST_SUCURSAL_ID, sucursal.getSucursalID());

        NavHostFragment.findNavController(this)
           .navigate(R.id.action_nav_detalle_sucursalu_to_nav_editpedido, datos);
    }

    private void mbAdminProductosClic() {
        //Implementar la acción para administrar los productos de la sucursal
    }

    private void mbEliminarSucursalClic() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        alertDialogBuilder.setMessage(R.string.msj_eliminar_sucursal);
        alertDialogBuilder.setPositiveButton(R.string.action_aceptar, (dialog, which) -> {
            //implementar la eliminación tanto de la sucursal como de sus productos
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
        String nombre = tilNombre.getEditText().getText().toString();
        String direccion = tilDireccion.getEditText().getText().toString();
        String horaApertura = tilHoraApertura.getEditText().getText().toString();
        String horaCierre = tilHoraCierre.getEditText().getText().toString();

        if (esSucursalNueva) {
            SucursalModelo sucursalNueva = new SucursalModelo();
            sucursalNueva.setNegocioID(AccionesFirebaseAuth.getUID());
            sucursalNueva.setSucursalID(UUID.randomUUID().toString());
            sucursalNueva.setRemoteImg();
            sucursalNueva.setNombre(nombre);
            sucursalNueva.setDireccion(direccion);
            sucursalNueva.setHoraAper(horaApertura);
            sucursalNueva.setHoraCierre(horaCierre);

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
        else {
            SucursalModelo sucursalEditada = new SucursalModelo();
            sucursalEditada.setNegocioID(this.sucursal.getNegocioID());
            sucursalEditada.setSucursalID(this.sucursal.getSucursalID());
            sucursalEditada.setRemoteImg();
            sucursalEditada.setNombre(nombre);
            sucursalEditada.setHoraAper(horaApertura);
            sucursalEditada.setHoraCierre(horaCierre);
            sucursalEditada.setDireccion(this.sucursal.getDireccion());
            boolean direccionHaCambiado = ! this.sucursal.getDireccion().equals(direccion);

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
                guardaDatosSucursal(sucursalEditada, R.string.msj_guardando_datos);
            }
        }
    }

    private void latitudLongitudDeDireccion(String direccion, CallbackGeneral<Address> callbackGeneral) {
        callbackGeneral.enInicio();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addressList;

            try {
                addressList = geocoder.getFromLocationName(direccion, 1);
                callbackGeneral.enExito(addressList.get(0));
            }
            catch (IOException ioException) {
                callbackGeneral.enFallo(ioException);
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

}