package com.example.tiendita.ui.pedidos;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

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
import com.example.tiendita.datos.firebase.UploadCallback;
import com.example.tiendita.datos.modelos.NegocioModelo;
import com.example.tiendita.datos.modelos.PedidoModelo;
import com.example.tiendita.datos.modelos.ProductosPedidoModelo;
import com.example.tiendita.datos.modelos.SucursalModelo;
import com.example.tiendita.datos.modelos.UsuarioModelo;
import com.example.tiendita.utilidades.Constantes;
import com.example.tiendita.utilidades.ImageManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetallePedidoFragment extends Fragment implements FirebaseCallback<DataSnapshot>,
        DownloadCallback,
        View.OnClickListener {

    private DetallePedidoViewModel mViewModel;
    private ImageView ivImagen;
    private TextView tvNombre,tvID,tvTotal,tvFecha,tvDireccion;
    private ListView listView;
    private Button bttnEditar,bttnCancelar;
    private Boolean esNegocio,banDialogo;
    private PedidoModelo currentPedido;
    private SucursalModelo currentSucursal;
    private ProductosPedidoModelo currentProductosPedidoModelo;
    private UsuarioModelo currentUser;
    private ArrayList<ProductosPedidoModelo> lista;

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
            currentPedido.setClienteID(data.getString(Constantes.CONST_PEDIDO_CLIENTE_ID));
            currentPedido.setFecha(data.getString(Constantes.CONST_PEDIDO_FECHA));
            currentPedido.setHora(data.getString(Constantes.CONST_PEDIDO_HORA));
            currentPedido.setNegocioID(data.getString(Constantes.CONST_PEDIDO_NEGOCIO_ID));
            currentPedido.setPago(data.getFloat(Constantes.CONST_PEDIDO_PAGO));
            currentPedido.setPedidoID(data.getString(Constantes.CONST_PEDIDO_ID));
            currentPedido.setSucursalID(data.getString(Constantes.CONST_PEDIDO_SUCURSAL_ID));
            currentPedido.setTotalProductos(data.getInt(Constantes.CONST_PEDIDO_TOTAL_PROD));
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
        bttnEditar.setOnClickListener(this);
        bttnCancelar.setOnClickListener(this);

        if (esNegocio) {
            bttnEditar.setVisibility(View.GONE);
            AccionesFirebaseRTDataBase.getUser(currentPedido.getClienteID(),
                    this);

        } else {
            bttnEditar.setOnClickListener(this);
            AccionesFirebaseRTDataBase.getSucursal(currentPedido.getNegocioID(),currentPedido.getSucursalID(),
                    this);

        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DetallePedidoViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void enInicio() {

    }

    @Override
    public void enExito(DataSnapshot respuesta, int tipo) {
        switch (tipo) {
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
                    AccionesFirebaseRTDataBase.getListaProductosPedido(currentPedido.getPedidoID(),this);
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
                    AccionesFirebaseRTDataBase.getListaProductosPedido(currentPedido.getPedidoID(),this);
                } else {
                    AccionesFireStorage.downloadImg(currentSucursal.getRemoteImg(),
                            this.getActivity(),
                            this.getContext(),
                            this,
                            currentSucursal.getSucursalID());
                }




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
                showData();

            }
                break;
        }

    }

    private void showData() {
        if(esNegocio){
            String localRef=AccionesFirebaseRTDataBase.getLocalImgRef(currentUser.getId(),getContext());
            ImageManager.loadImage(localRef,ivImagen,this.getContext());
            tvNombre.setText("Cliente: "+currentUser.getNombre()+" "+currentUser.getApellido());
            tvDireccion.setText("Sucursal:"+currentSucursal.getNombre());
        }else{
            String localRef=AccionesFirebaseRTDataBase.getLocalImgRef(currentSucursal.getSucursalID(),getContext());
            ImageManager.loadImage(localRef,ivImagen,this.getContext());
            tvNombre.setText("Negocio: "+currentSucursal.getNombre());
            tvDireccion.setText("Dirección:"+currentSucursal.getDireccion());
        }

        tvID.setText("ID:"+currentPedido.getPedidoID());
        tvFecha.setText(currentPedido.getFecha()+"\n"+currentPedido.getHora());
        tvTotal.setText("Total:" +currentPedido.getPago());

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
        Toast.makeText(this.getContext(), R.string.fallo_pedido, Toast.LENGTH_LONG).show();

    }
    //descarga de imagen remota
    @Override
    public void enInicioDesc() {

    }

    @Override
    public void enExitoDesc(Object respuesta, File localFile) {
        //se guarda la nueva direccion local
        //y se muestran los datos
        if(banDialogo){
            showDialog(currentProductosPedidoModelo);
        }else{
            AccionesFirebaseRTDataBase.getListaProductosPedido(currentPedido.getPedidoID(),this);

        }

    }

    @Override
    public void enFalloDesc(Exception excepcion) {
        Toast.makeText(this.getContext(), R.string.error_cargar_img,Toast.LENGTH_LONG).show();
        Log.d("Descargar imagen","Error al descargar imagen\n Causa: "+excepcion.getCause());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bttn_editar_detalle: {
                Bundle data = new Bundle();

                data.putBoolean(Constantes.CONST_EDICION_TYPE, true);
                data.putString(Constantes.CONST_PEDIDO_ID, currentPedido.getPedidoID());
                data.putString(Constantes.CONST_PEDIDO_NEGOCIO_ID, currentPedido.getNegocioID());
                data.putString(Constantes.CONST_PEDIDO_SUCURSAL_ID, currentPedido.getSucursalID());
                data.putString(Constantes.CONST_PEDIDO_CLIENTE_ID, currentPedido.getClienteID());
                data.putString(Constantes.CONST_PEDIDO_FECHA, currentPedido.getFecha());
                data.putString(Constantes.CONST_PEDIDO_HORA, currentPedido.getHora());
                data.putFloat(Constantes.CONST_PEDIDO_PAGO, currentPedido.getPago());
                data.putInt(Constantes.CONST_PEDIDO_TOTAL_PROD, currentPedido.getTotalProductos());

                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_nav_pedidou_to_nav_editpedido, data);
            }
                break;
            case R.id.bttn_cancelar_detalle:{
            }

                break;
        }


    }
}