package com.example.tiendita.ui.pedidos;

import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
        DownloadCallback{

    private DetallePedidoViewModel mViewModel;
    private ImageView ivImagen;
    private TextView tvNombre,tvID,tvTotal,tvFecha,tvDireccion;
    private ListView listView;
    private Button bttnEditar,bttnCancelar;
    private Boolean esNegocio;
    private  String currentId;
    private PedidoModelo currentPedido;
    private SucursalModelo currentSucursal;
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
            currentId=data.getString(Constantes.CONST_PEDIDO_ID);
            init(root);
        }else{
            esNegocio=false;
        }

        return root;
    }

    private void init(View root) {
        ivImagen=root.findViewById(R.id.iv_imagen_detalle);
        tvNombre=root.findViewById(R.id.tv_nombre_detalle);
        tvID=root.findViewById(R.id.tv_id_pedido_detalle);
        tvTotal= root.findViewById(R.id.tv_total_detalle);
        tvFecha=root.findViewById(R.id.tv_fecha_detalle);
        listView=root.findViewById(R.id.lv_productos_detalle);
        tvDireccion=root.findViewById(R.id.tv_direccion_detalle);
        bttnCancelar=root.findViewById(R.id.bttn_cancelar_detalle);
        bttnEditar=root.findViewById(R.id.bttn_editar_detalle);

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

    }

    @Override
    public void enExito(DataSnapshot respuesta, int tipo) {
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
                currentUser.setLocalImg(cliente.get(Constantes.CONST_BASE_LOCALIMG).toString());
                currentUser.setRemoteImg(cliente.get(Constantes.CONST_BASE_REMOTEIMG).toString());

                File filePhoto = new File(currentUser.getLocalImg());
                if (filePhoto.exists()) {
                    AccionesFirebaseRTDataBase.getListaProductosPedido(currentId,this);
                } else {
                    AccionesFireStorage.downloadImg(currentUser.getRemoteImg(),
                            this.getActivity(),
                            this.getContext(),
                            this,
                            currentUser.getId(),
                            Constantes.UPDATE_LOCALIMG_CLIENTE);
                }
            }
                break;
            case AccionesFirebaseRTDataBase.GET_SUCURSAL_ACCTION: {
                HashMap cliente = (HashMap) respuesta.getValue();
                currentSucursal = new SucursalModelo();
                currentSucursal.setSucursalID(cliente.get(Constantes.CONST_SUCURSAL_ID).toString());
                currentSucursal.setLocalImg(cliente.get(Constantes.CONST_BASE_LOCALIMG).toString());
                currentSucursal.setRemoteImg(cliente.get(Constantes.CONST_BASE_REMOTEIMG).toString());
                currentSucursal.setNegocioID(cliente.get(Constantes.CONST_NEGOCIO_ID).toString());
                currentSucursal.setHoraCierre(cliente.get(Constantes.CONST_SUCURSAL_HORACIERRE).toString());
                currentSucursal.setHoraAper(cliente.get(Constantes.CONST_SUCURSAL_HORAAPER).toString());
                currentSucursal.setDireccion(cliente.get(Constantes.CONST_SUCURSAL_DIRECCION).toString());
                currentSucursal.setLatitud(Double.parseDouble(cliente.get(Constantes.CONST_SUCURSAL_LAT).toString()));
                currentSucursal.setLongitud(Double.parseDouble(cliente.get(Constantes.CONST_SUCURSAL_LONG).toString()));
                currentSucursal.setNombre(cliente.get(Constantes.CONST_SUCURSAL_NOMBRE).toString());


                File filePhoto = new File(currentSucursal.getLocalImg());
                if (filePhoto.exists()) {
                    AccionesFirebaseRTDataBase.getListaProductosPedido(currentId,this);
                } else {
                    AccionesFireStorage.downloadImg(currentSucursal.getRemoteImg(),
                            this.getActivity(),
                            this.getContext(),
                            this,
                            currentSucursal.getSucursalID(),
                            Constantes.UPDATE_LOCALIMG_SUCURSAL);
                }



                showData();

            }
            break;
            case AccionesFirebaseRTDataBase.GET_LISTA_PRODUCTOS_PEDIDO_ACCTION: {
                for (DataSnapshot dataSnapshot : respuesta.getChildren()) {
                    ProductosPedidoModelo productosPedidoModelo=new ProductosPedidoModelo();
                    productosPedidoModelo.setCantidad(Integer.parseInt(dataSnapshot.child(Constantes.CONST_PRODUCTO_CANTIDAD).getValue().toString()));
                    productosPedidoModelo.setDescripcion(dataSnapshot.child(Constantes.CONST_PRODUCTO_DESCRIPCION).getValue().toString());
                    productosPedidoModelo.setLocalImg(dataSnapshot.child(Constantes.CONST_BASE_LOCALIMG).getValue().toString());
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
            ImageManager.loadImage(currentUser.getLocalImg(),ivImagen,this.getContext());
            tvNombre.setText("Cliente: "+currentUser.getNombre()+" "+currentUser.getApellido());
            tvID.setText("ID:"+currentId);
            tvFecha.setText(currentPedido.getFecha()+"\n"+currentPedido.getHora());
            tvDireccion.setText("Sucursal:"+currentSucursal.getNombre());
            tvTotal.setText("Total:" +currentPedido.getPago());
        }else{
            ImageManager.loadImage(currentSucursal.getLocalImg(),ivImagen,this.getContext());
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
                //redirect vista de pedido
                /*
                    Bundle data = new Bundle();
                    data.putString(Constantes.CONST_PEDIDO_ID,listaPedidos.get(i).getPedidoID());
                    data.putBoolean(Constantes.CONST_NEGOCIO_TYPE,esNegocio);
                    if(esNegocio){
                    NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_nav_listar_to_nav_editar, data);
                    }else{
                    NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_nav_listar_to_nav_editar, data);
                    }
                                    */
            }

        });


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
        if(esNegocio){
            currentUser.setLocalImg(localFile.getAbsolutePath());

        }else {
            currentSucursal.setLocalImg(localFile.getAbsolutePath());

        }
        AccionesFirebaseRTDataBase.getListaProductosPedido(currentId,this);
    }

    @Override
    public void enFalloDesc(Exception excepcion) {
        Toast.makeText(this.getContext(), R.string.error_cargar_img,Toast.LENGTH_LONG).show();
        Log.d("Descargar imagen","Error al descargar imagen\n Causa: "+excepcion.getCause());
    }




}