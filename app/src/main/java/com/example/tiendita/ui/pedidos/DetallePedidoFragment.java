package com.example.tiendita.ui.pedidos;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFireStorage;
import com.example.tiendita.datos.firebase.AccionesFirebaseRTDataBase;
import com.example.tiendita.datos.firebase.DownloadCallback;
import com.example.tiendita.datos.firebase.FirebaseCallback;
import com.example.tiendita.datos.modelos.PedidoModelo;
import com.example.tiendita.datos.modelos.ProductosPedidoModelo;
import com.example.tiendita.datos.modelos.SucursalModelo;
import com.example.tiendita.datos.modelos.UsuarioModelo;
import com.example.tiendita.utilidades.Constantes;
import com.example.tiendita.utilidades.Dialogo;
import com.example.tiendita.utilidades.ImageManager;
import com.google.firebase.database.DataSnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DetallePedidoFragment extends Fragment implements FirebaseCallback<DataSnapshot>,
        DownloadCallback,
        View.OnClickListener {

    private DetallePedidoViewModel mViewModel;
    private ImageView ivImagen;
    private TextView tvNombre,tvID,tvTotal,tvFecha,tvDireccion;
    private ListView listView;
    private Button bttnEditar,bttnCancelar;
    private Boolean esNegocio,banDialogo;
    private  String currentId;
    private PedidoModelo currentPedido;
    private SucursalModelo currentSucursal;
    private ProductosPedidoModelo currentProductosPedidoModelo;
    private UsuarioModelo currentUser;
    private ArrayList<ProductosPedidoModelo> lista;
    private AlertDialog alertDialog;

    public static DetallePedidoFragment newInstance() {
        return new DetallePedidoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_detalle_pedido, container, false);
        Bundle data = this.getArguments();
        if (data != null) {
            esNegocio=data.getBoolean(Constantes.CONST_NEGOCIO_TYPE);
            currentId=data.getString(Constantes.CONST_PEDIDO_ID);
            init(root);
        }else{
            esNegocio=false;
        }

        return root;
    }

    private void init(View root) {
        banDialogo=false;
        ivImagen=root.findViewById(R.id.iv_imagen_detalle);
        tvNombre=root.findViewById(R.id.tv_nombre_detalle);
        tvID=root.findViewById(R.id.tv_id_pedido_detalle);
        tvTotal= root.findViewById(R.id.tv_total_detalle);
        tvFecha=root.findViewById(R.id.tv_fecha_detalle);
        listView=root.findViewById(R.id.lv_productos_detalle);
        tvDireccion=root.findViewById(R.id.tv_direccion_detalle);
        bttnCancelar=root.findViewById(R.id.bttn_cancelar_detalle);
        bttnEditar=root.findViewById(R.id.bttn_editar_detalle);
        bttnCancelar.setOnClickListener(this);
        if(esNegocio){
            bttnEditar.setVisibility(View.GONE);
        }else{
            bttnEditar.setOnClickListener(this);
        }

        AccionesFirebaseRTDataBase.getPedido(currentId,
                                            this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DetallePedidoViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void enInicio() {
        alertDialog = Dialogo.dialogoProceso(getView(), R.string.msj_cargando_detalles_pedido);
        Dialogo.muestraDialogoProceso(alertDialog);
    }

    @Override
    public void enExito(DataSnapshot respuesta, int tipo) {
        Dialogo.ocultaDialogoProceso(alertDialog);

        switch (tipo) {
            case AccionesFirebaseRTDataBase.GET_PEDIDO_ACCTION: {
                HashMap pedidoHash = (HashMap) respuesta.getValue();
                currentPedido.setClienteID(pedidoHash.get(Constantes.CONST_PEDIDO_CLIENTE_ID).toString());
                currentPedido.setFecha(pedidoHash.get(Constantes.CONST_PEDIDO_FECHA).toString());
                currentPedido.setHora(pedidoHash.get(Constantes.CONST_PEDIDO_HORA).toString());
                currentPedido.setNegocioID(pedidoHash.get(Constantes.CONST_PEDIDO_NEGOCIO_ID).toString());
                currentPedido.setPago(Float.parseFloat(pedidoHash.get(Constantes.CONST_PEDIDO_PAGO).toString()));
                currentPedido.setPedidoID(pedidoHash.get(Constantes.CONST_PEDIDO_ID).toString());
                currentPedido.setSucursalID(pedidoHash.get(Constantes.CONST_PEDIDO_SUCURSAL_ID).toString());
                currentPedido.setTotalProductos(Integer.parseInt(pedidoHash.get(Constantes.CONST_PEDIDO_TOTAL_PROD).toString()));
                if (esNegocio) {
                    AccionesFirebaseRTDataBase.getUser(currentPedido.getClienteID(),
                            this);

                } else {
                    AccionesFirebaseRTDataBase.getNegocio(currentPedido.getNegocioID(),
                            this);

                }
            }
            break;
            case AccionesFirebaseRTDataBase.GET_USER_ACCTION: {
                HashMap cliente = (HashMap) respuesta.getValue();
                currentUser = new UsuarioModelo();
                currentUser.setId(cliente.get(Constantes.CONST_BASE_ID).toString());
                currentUser.setNombre(cliente.get(Constantes.CONST_BASE_NOMBRE).toString());
                currentUser.setApellido(cliente.get(Constantes.CONST_BASE_APELLIDO).toString());
                currentUser.setContrasenia(cliente.get(Constantes.CONST_BASE_CONTRASENIA).toString());
                currentUser.setRemoteImg(cliente.get(Constantes.CONST_BASE_REMOTEIMG).toString());
                String localRef=AccionesFirebaseRTDataBase.getLocalImgRef(currentUser.getId(),getContext());
                File filePhoto = new File(localRef);
                if (filePhoto.exists()) {
                    AccionesFirebaseRTDataBase.getListaProductosPedido(currentId,this);
                } else {
                    AccionesFireStorage.downloadImg(currentUser.getRemoteImg(),
                            this.getActivity(),
                            this.getContext(),
                            this,
                            currentUser.getId());
                }
            }
                break;
            case AccionesFirebaseRTDataBase.GET_SUCURSAL_ACCTION: {
                HashMap cliente = (HashMap) respuesta.getValue();
                currentSucursal = new SucursalModelo();
                currentSucursal.setSucursalID(cliente.get(Constantes.CONST_SUCURSAL_ID).toString());
                currentSucursal.setRemoteImg(cliente.get(Constantes.CONST_BASE_REMOTEIMG).toString());
                currentSucursal.setNegocioID(cliente.get(Constantes.CONST_NEGOCIO_ID).toString());
                currentSucursal.setHoraCierre(cliente.get(Constantes.CONST_SUCURSAL_HORACIERRE).toString());
                currentSucursal.setHoraAper(cliente.get(Constantes.CONST_SUCURSAL_HORAAPER).toString());
                currentSucursal.setDireccion(cliente.get(Constantes.CONST_SUCURSAL_DIRECCION).toString());
                currentSucursal.setLatitud(Double.parseDouble(cliente.get(Constantes.CONST_SUCURSAL_LAT).toString()));
                currentSucursal.setLongitud(Double.parseDouble(cliente.get(Constantes.CONST_SUCURSAL_LONG).toString()));
                currentSucursal.setNombre(cliente.get(Constantes.CONST_SUCURSAL_NOMBRE).toString());

                String localRef=AccionesFirebaseRTDataBase.getLocalImgRef(currentSucursal.getSucursalID(),getContext());
                File filePhoto = new File(localRef);
                if (filePhoto.exists()) {
                    AccionesFirebaseRTDataBase.getListaProductosPedido(currentId,this);
                } else {
                    AccionesFireStorage.downloadImg(currentSucursal.getRemoteImg(),
                            this.getActivity(),
                            this.getContext(),
                            this,
                            currentSucursal.getSucursalID());
                }
                showData();

            }
            break;
            case AccionesFirebaseRTDataBase.GET_LISTA_PRODUCTOS_PEDIDO_ACCTION: {
                for (DataSnapshot dataSnapshot : respuesta.getChildren()) {
                    ProductosPedidoModelo productosPedidoModelo=new ProductosPedidoModelo();
                    productosPedidoModelo.setCantidad(Integer.parseInt(dataSnapshot.child(Constantes.CONST_PRODUCTO_CANTIDAD).getValue().toString()));
                    productosPedidoModelo.setDescripcion(dataSnapshot.child(Constantes.CONST_PRODUCTO_DESCRIPCION).getValue().toString());
                     productosPedidoModelo.setNombreProducto(dataSnapshot.child(Constantes.CONST_PRODUCTO_NOMBRE).getValue().toString());
                    productosPedidoModelo.setPedidoID(dataSnapshot.child(Constantes.CONST_PEDIDO_ID).getValue().toString());
                    productosPedidoModelo.setPrecio(Float.parseFloat(dataSnapshot.child(Constantes.CONST_PRODUCTO_PRECIO).getValue().toString()));
                    productosPedidoModelo.setRemoteImg(dataSnapshot.child(Constantes.CONST_BASE_REMOTEIMG).getValue().toString());
                    productosPedidoModelo.setProductoId(dataSnapshot.child(Constantes.CONST_PRODUCTO_ID).getValue().toString());
                    productosPedidoModelo.setSucursalId(dataSnapshot.child(Constantes.CONST_PRODUCTO_SUCURSAL_ID).getValue().toString());
                    lista.add(productosPedidoModelo);
                }
                AccionesFirebaseRTDataBase.getSucursal(currentPedido.getSucursalID(),this);

            }
                break;
        }

    }

    private void showData() {
        if(esNegocio){
            String localRef=AccionesFirebaseRTDataBase.getLocalImgRef(currentUser.getId(),getContext());
            ImageManager.loadImage(localRef,ivImagen,this.getContext());
            tvNombre.setText("Cliente: "+currentUser.getNombre()+" "+currentUser.getApellido());
            tvID.setText("ID:"+currentId);
            tvFecha.setText(currentPedido.getFecha()+"\n"+currentPedido.getHora());
            tvDireccion.setText("Sucursal:"+currentSucursal.getNombre());
            tvTotal.setText("Total:" +currentPedido.getPago());
        }else{
            String localRef=AccionesFirebaseRTDataBase.getLocalImgRef(currentSucursal.getSucursalID(),getContext());
            ImageManager.loadImage(localRef,ivImagen,this.getContext());
            tvNombre.setText("Negocio: "+currentSucursal.getNombre());
            tvID.setText("ID:"+currentId);
            tvTotal.setText("Total:" +currentPedido.getPago());
            tvDireccion.setText("Dirección:"+currentSucursal.getDireccion());
            tvFecha.setText(currentPedido.getFecha()+"\n"+currentPedido.getHora());
        }

        ArrayAdapter<ProductosPedidoModelo> adapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1,
                lista);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,  int i, long l) {
                initDialog(i);
            }

        });


    }

    private void initDialog(int i) {
        banDialogo=true;
        currentProductosPedidoModelo=lista.get(i);
        String localRef=AccionesFirebaseRTDataBase.getLocalImgRef(currentProductosPedidoModelo.getProductoId(),getContext());
        File filePhoto = new File(localRef);
        if (filePhoto.exists()) {
            showDialog(currentProductosPedidoModelo);
        } else {
            AccionesFireStorage.downloadImg(currentProductosPedidoModelo.getRemoteImg(),
                    getActivity(),
                    getContext(),
                    this,
                    currentProductosPedidoModelo.getProductoId());
        }
    }

    private void showDialog(ProductosPedidoModelo productosPedidoModelo) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialogo_layout,null);
        ((TextView) dialogView.findViewById(R.id.info_producto_lista)).setText(productosPedidoModelo.getDescripcion());
        ImageView imagen = dialogView.findViewById(R.id.foto_producto_lista);
        String localRef=AccionesFirebaseRTDataBase.getLocalImgRef(productosPedidoModelo.getProductoId(),getContext());

        ImageManager.loadImage(localRef,imagen,this.getContext());
        AlertDialog.Builder dialogo= new AlertDialog.Builder(getContext());
        dialogo.setTitle(R.string.header_producto);
        dialogo.setView(dialogView);
        dialogo.show();
    }


    @Override
    public void enFallo(Exception excepcion) {
        Dialogo.ocultaDialogoProceso(alertDialog);
        Toast.makeText(this.getContext(), R.string.fallo_pedido, Toast.LENGTH_LONG).show();

    }
    //descarga de imagen remota
    @Override
    public void enInicioDesc() {
        alertDialog = Dialogo.dialogoProceso(getView(), R.string.msj_descargando_datos_pedido);
        Dialogo.muestraDialogoProceso(alertDialog);
    }

    @Override
    public void enExitoDesc(Object respuesta, File localFile) {
        //se guarda la nueva direccion local
        //y se muestran los datos
        Dialogo.ocultaDialogoProceso(alertDialog);

        if(banDialogo){
            showDialog(currentProductosPedidoModelo);
        }else{
            AccionesFirebaseRTDataBase.getListaProductosPedido(currentId,this);
        }

    }

    @Override
    public void enFalloDesc(Exception excepcion) {
        Dialogo.ocultaDialogoProceso(alertDialog);
        Toast.makeText(this.getContext(), R.string.error_cargar_img,Toast.LENGTH_LONG).show();
        Log.d("Descargar imagen","Error al descargar imagen\n Causa: "+excepcion.getCause());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bttn_editar_detalle:
                Bundle data = new Bundle();
                data.putBoolean(Constantes.CONST_EDICION_TYPE,true);
                data.putString(Constantes.CONST_PEDIDO_ID,currentId);
                data.putString(Constantes.CONST_SUCURSAL_ID,currentSucursal.getSucursalID());
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_pedidou_to_nav_editpedido, data);
                break;
            case R.id.bttn_cancelar_detalle:
                break;

        }

    }
}